package _ErrorClub.example.demo.solicitacao.dto;

import _ErrorClub.example.demo.solicitacao.enums.StatusSolicitacao;
import _ErrorClub.example.demo.solicitacao.enums.TipoSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResponse {
    private UUID id;
    private String emailAluno;
    private String nomeAluno;
    private UUID professorId;
    private String descricao;
    private LocalDate dataOcorrencia;
    private TipoSolicitacao tipo;
    private StatusSolicitacao status;
    private String disciplina;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
