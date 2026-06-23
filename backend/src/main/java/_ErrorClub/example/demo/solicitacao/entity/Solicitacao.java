package _ErrorClub.example.demo.solicitacao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "solicitacoes")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    private UUID alunoId;

    private UUID professorId;

    private String descricao;

    private LocalDate dataOcorrencia;

    private String tipo;

    private String status;

    private String disciplina;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
