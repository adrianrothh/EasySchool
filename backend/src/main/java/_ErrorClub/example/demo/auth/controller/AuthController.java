package _ErrorClub.example.demo.auth.controller;

import _ErrorClub.example.demo.auth.dto.LoginRequest;
import _ErrorClub.example.demo.auth.dto.RefreshRequest;
import _ErrorClub.example.demo.auth.dto.TokenPair;
import _ErrorClub.example.demo.auth.dto.TokenResponse;
import _ErrorClub.example.demo.auth.service.AuthService;
import jakarta.validation.Valid;
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

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenPair tokens = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new TokenResponse(tokens.getAccessToken(), tokens.getRefreshToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair tokens = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(new TokenResponse(tokens.getAccessToken(), tokens.getRefreshToken()));
    }
}
