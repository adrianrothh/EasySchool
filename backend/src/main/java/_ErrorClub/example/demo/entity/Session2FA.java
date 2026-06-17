package _ErrorClub.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "sessoes_2fa")
public class Session2FA {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    private UUID usuarioId;

    private boolean verified = false;

    private OffsetDateTime expiresAt;

    private short attempts = 0;

    private String otpHash;

    private OffsetDateTime createdAt;
}