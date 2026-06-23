package _ErrorClub.example.demo.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    private UUID usuarioId;

    private OffsetDateTime expiresAt;

    private boolean revoked = false;

    private String userAgent;

    private String tokenHash;

    private OffsetDateTime createdAt;
}
