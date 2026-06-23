package _ErrorClub.example.demo.auth.controller;

import _ErrorClub.example.demo.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    record LoginRequest(String email, String password) {}
    record RefreshRequest(String refreshToken) {}
    record TokenResponse(String accessToken, String refreshToken) {}

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        AuthService.TokenPair tokens = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new TokenResponse(tokens.accessToken(), tokens.refreshToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        AuthService.TokenPair tokens = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(new TokenResponse(tokens.accessToken(), tokens.refreshToken()));
    }
}
