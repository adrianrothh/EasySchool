package _ErrorClub.example.demo.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name  = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    private String name;

    private String email;

    private String cpf;

    private String password;

    private String totpSecretHash;

    private boolean totpActivate = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Perfil perfil;

    private boolean activate = true;

    private short loginAttempt = 0;

    private OffsetDateTime blockedTo;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
