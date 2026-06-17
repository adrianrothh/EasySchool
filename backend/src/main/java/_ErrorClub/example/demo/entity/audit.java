package _ErrorClub.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    private UUID usuarioId;

    private String recursoId;

    private String userAgent;

    private String detalhe;

    private String evento;

    private String recursoTipo;

    private String checksum;

    private OffsetDateTime createdAt;
}