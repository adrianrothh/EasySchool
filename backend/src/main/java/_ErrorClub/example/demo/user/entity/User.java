package _ErrorClub.example.demo.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private String cpf;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String totpSecretHash;

    @JsonIgnore
    private boolean totpActivate = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Perfil perfil;

    @JsonIgnore
    private boolean activate = true;

    @JsonIgnore
    private short loginAttempt = 0;

    @JsonIgnore
    private OffsetDateTime blockedTo;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
