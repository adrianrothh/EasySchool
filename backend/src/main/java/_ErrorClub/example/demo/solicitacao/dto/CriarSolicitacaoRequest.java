package _ErrorClub.example.demo.solicitacao.dto;

import java.time.LocalDate;
import java.util.UUID;

public class CriarSolicitacaoRequest {
    public String descricao;
    public LocalDate dataOcorrencia;
    public String tipo;
    public String disciplina;
    public UUID professorId;
}
