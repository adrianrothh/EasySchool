package _ErrorClub.example.demo.auth.service;

import _ErrorClub.example.demo.auth.entity.RefreshToken;
import _ErrorClub.example.demo.auth.repository.RefreshTokenRepository;
import _ErrorClub.example.demo.user.entity.User;
import _ErrorClub.example.demo.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public record TokenPair(String accessToken, String refreshToken) {}

    public TokenPair login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!user.isActivate()) {
            throw new RuntimeException("Usuário desativado");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return generateTokens(user);
    }

    public TokenPair refresh(String rawRefreshToken) {
        String hash = hashToken(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Refresh token expirado ou revogado");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = userRepository.findById(stored.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return generateTokens(user);
    }

    private TokenPair generateTokens(User user) {
        String role = user.getPerfil().getNome();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), role);

        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuarioId(user.getId());
        refreshToken.setTokenHash(hashToken(rawRefreshToken));
        refreshToken.setExpiresAt(OffsetDateTime.now().plusDays(7));
        refreshToken.setCreatedAt(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);

        return new TokenPair(accessToken, rawRefreshToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
