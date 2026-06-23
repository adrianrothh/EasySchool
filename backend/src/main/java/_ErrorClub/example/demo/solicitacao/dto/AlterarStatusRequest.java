package _ErrorClub.example.demo.solicitacao.dto;

import _ErrorClub.example.demo.solicitacao.enums.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarStatusRequest {
    private StatusSolicitacao status;
    private String textoParecer;
}
