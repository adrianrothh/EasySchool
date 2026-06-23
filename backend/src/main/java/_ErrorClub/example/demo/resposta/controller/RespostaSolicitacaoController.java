package _ErrorClub.example.demo.resposta.controller;

import _ErrorClub.example.demo.resposta.entity.RespostaSolicitacao;
import _ErrorClub.example.demo.resposta.service.RespostaSolicitacaoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/respostas")
public class RespostaSolicitacaoController {

    private final RespostaSolicitacaoService service;

    public RespostaSolicitacaoController(RespostaSolicitacaoService service) {
        this.service = service;
    }

    @GetMapping("/solicitacao/{solicitacaoId}")
    @PreAuthorize("hasAnyRole('PROFESSOR','ADMIN')")
    public List<RespostaSolicitacao> porSolicitacao(@PathVariable UUID solicitacaoId) {
        return service.listarPorSolicitacao(solicitacaoId);
    }
}