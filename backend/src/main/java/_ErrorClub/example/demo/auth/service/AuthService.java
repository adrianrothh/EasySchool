package _ErrorClub.example.demo.auth.service;

import _ErrorClub.example.demo.audit.enums.AuditEvento;
import _ErrorClub.example.demo.audit.service.AuditLogService;
import _ErrorClub.example.demo.auth.dto.TokenPair;
import _ErrorClub.example.demo.auth.entity.RefreshToken;
import _ErrorClub.example.demo.auth.repository.RefreshTokenRepository;
import _ErrorClub.example.demo.user.entity.User;
import _ErrorClub.example.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private static final short MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

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
                    "Credenciais inválidas",
                    null, null, "USER");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        if (!user.isActivate()) {
            auditLogService.registrar(
                    AuditEvento.AUTH_LOGIN_FAIL,
                    "Conta desativada",
                    user.getId(), user.getId().toString(), "USER");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta desativada");
        }

        if (isAccountLocked(user)) {
            auditLogService.registrar(
                    AuditEvento.AUTH_LOGIN_FAIL,
                    "Conta bloqueada temporariamente",
                    user.getId(), user.getId().toString(), "USER");
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Conta bloqueada. Tente novamente em " + LOCKOUT_MINUTES + " minutos");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            incrementLoginAttempts(user);
            auditLogService.registrar(
                    AuditEvento.AUTH_LOGIN_FAIL,
                    "Credenciais inválidas",
                    user.getId(), user.getId().toString(), "USER");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        resetLoginAttempts(user);

        TokenPair tokens = generateTokens(user);
        auditLogService.registrar(
                AuditEvento.AUTH_LOGIN_SUCCESS,
                "Login bem-sucedido",
                user.getId(), user.getId().toString(), "USER");
        return tokens;
    }

    public TokenPair refresh(String rawRefreshToken) {
        String hash = hashToken(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash).orElse(null);

        if (stored == null) {
            auditLogService.registrar(
                    AuditEvento.AUTH_REFRESH_FAIL,
                    "Refresh token não encontrado",
                    null, null, "REFRESH_TOKEN");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
        }

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
            auditLogService.registrar(
                    AuditEvento.AUTH_REFRESH_FAIL,
                    "Refresh token expirado ou revogado",
                    stored.getUsuarioId(), stored.getId().toString(), "REFRESH_TOKEN");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado ou revogado");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = userRepository.findById(stored.getUsuarioId())
                .orElseThrow(() -> {
                    auditLogService.registrar(
                            AuditEvento.AUTH_REFRESH_FAIL,
                            "Usuário do refresh token não encontrado",
                            stored.getUsuarioId(), stored.getId().toString(), "REFRESH_TOKEN");
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
                });

        TokenPair tokens = generateTokens(user);
        auditLogService.registrar(
                AuditEvento.AUTH_REFRESH_SUCCESS,
                "Refresh token rotacionado",
                user.getId(), user.getId().toString(), "USER");
        return tokens;
    }

    private boolean isAccountLocked(User user) {
        return user.getBlockedTo() != null && user.getBlockedTo().isAfter(OffsetDateTime.now());
    }

    private void incrementLoginAttempts(User user) {
        short attempts = (short) (user.getLoginAttempt() + 1);
        user.setLoginAttempt(attempts);
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setBlockedTo(OffsetDateTime.now().plusMinutes(LOCKOUT_MINUTES));
        }
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    private void resetLoginAttempts(User user) {
        if (user.getLoginAttempt() > 0 || user.getBlockedTo() != null) {
            user.setLoginAttempt((short) 0);
            user.setBlockedTo(null);
            user.setUpdatedAt(OffsetDateTime.now());
            userRepository.save(user);
        }
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
