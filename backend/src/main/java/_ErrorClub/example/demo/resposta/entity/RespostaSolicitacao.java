package _ErrorClub.example.demo.resposta.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "respostas_solicitacoes")
public class RespostaSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    private UUID solicitacaoId;

    private UUID autorId;

    private String texto;

    private String decisao;

    private OffsetDateTime createdAt;
}
