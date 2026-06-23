package _ErrorClub.example.demo.solicitacao.dto;

// O professor envia a decisão + o texto do parecer juntos.
public class AlterarStatusRequest {
    public String status;       // APROVADA ou REPROVADA
    public String textoParecer; // justificativa da decisão
}