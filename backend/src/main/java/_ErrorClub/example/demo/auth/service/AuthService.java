package _ErrorClub.example.demo.auth.service;

import _ErrorClub.example.demo.audit.enums.AuditEvento;
import _ErrorClub.example.demo.audit.service.AuditLogService;
import _ErrorClub.example.demo.auth.dto.TokenPair;
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
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenRepository refreshTokenRepository,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditLogService = auditLogService;
    }

    public TokenPair login(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            auditLogService.registrar(
                    AuditEvento.AUTH_LOGIN_FAIL,
                    "Usuário não encontrado: " + email,
                    null,
                    null,
                    "USER");
            throw new RuntimeException("Credenciais inválidas");
        }

        if (!user.isActivate()) {
            auditLogService.registrar(
                    AuditEvento.AUTH_LOGIN_FAIL,
                    "Usuário desativado: " + email,
                    user.getId(),
                    user.getId().toString(),
                    "USER");
            throw new RuntimeException("Usuário desativado");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            auditLogService.registrar(
                    AuditEvento.AUTH_LOGIN_FAIL,
                    "Senha incorreta para: " + email,
                    user.getId(),
                    user.getId().toString(),
                    "USER");
            throw new RuntimeException("Credenciais inválidas");
        }

        TokenPair tokens = generateTokens(user);
        auditLogService.registrar(
                AuditEvento.AUTH_LOGIN_SUCCESS,
                "Login bem-sucedido: " + email,
                user.getId(),
                user.getId().toString(),
                "USER");
        return tokens;
    }

    public TokenPair refresh(String rawRefreshToken) {
        String hash = hashToken(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash).orElse(null);

        if (stored == null) {
            auditLogService.registrar(
                    AuditEvento.AUTH_REFRESH_FAIL,
                    "Refresh token não encontrado",
                    null,
                    null,
                    "REFRESH_TOKEN");
            throw new RuntimeException("Refresh token inválido");
        }

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
            auditLogService.registrar(
                    AuditEvento.AUTH_REFRESH_FAIL,
                    "Refresh token expirado ou revogado",
                    stored.getUsuarioId(),
                    stored.getId().toString(),
                    "REFRESH_TOKEN");
            throw new RuntimeException("Refresh token expirado ou revogado");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = userRepository.findById(stored.getUsuarioId())
                .orElseThrow(() -> {
                    auditLogService.registrar(
                            AuditEvento.AUTH_REFRESH_FAIL,
                            "Usuário do refresh token não encontrado",
                            stored.getUsuarioId(),
                            stored.getId().toString(),
                            "REFRESH_TOKEN");
                    return new RuntimeException("Usuário não encontrado");
                });

        TokenPair tokens = generateTokens(user);
        auditLogService.registrar(
                AuditEvento.AUTH_REFRESH_SUCCESS,
                "Refresh token rotacionado",
                user.getId(),
                user.getId().toString(),
                "USER");
        return tokens;
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
