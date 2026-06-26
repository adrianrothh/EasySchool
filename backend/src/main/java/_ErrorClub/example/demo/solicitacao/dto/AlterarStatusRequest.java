package _ErrorClub.example.demo.solicitacao.dto;

import _ErrorClub.example.demo.solicitacao.enums.StatusSolicitacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarStatusRequest {

    @NotNull
    private StatusSolicitacao status;

    @Size(max = 2000)
    private String textoParecer;
}
