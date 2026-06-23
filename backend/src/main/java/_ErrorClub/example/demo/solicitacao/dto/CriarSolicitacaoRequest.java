package _ErrorClub.example.demo.solicitacao.dto;

import _ErrorClub.example.demo.solicitacao.enums.TipoSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarSolicitacaoRequest {
    private String descricao;
    private LocalDate dataOcorrencia;
    private TipoSolicitacao tipo;
    private String disciplina;
    private UUID professorId;
}
