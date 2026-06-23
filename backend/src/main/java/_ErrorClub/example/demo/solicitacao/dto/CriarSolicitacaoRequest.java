package _ErrorClub.example.demo.solicitacao.dto;

import java.time.LocalDate;
import java.util.UUID;

// Note que NÃO tem status nem alunoId: status nasce PENDENTE (no service)
// e o alunoId vem do usuário logado, nunca do corpo da requisição.
public class CriarSolicitacaoRequest {
    public String descricao;
    public LocalDate dataOcorrencia;
    public String tipo;        // REVISAO_NOTA ou ABONO_FALTA
    public String disciplina;
    public UUID professorId;   // professor a quem a solicitação se destina
}