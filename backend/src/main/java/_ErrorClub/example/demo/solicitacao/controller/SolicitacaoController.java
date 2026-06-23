package _ErrorClub.example.demo.solicitacao.controller;

import _ErrorClub.example.demo.infra.security.CurrentUserService;
import _ErrorClub.example.demo.solicitacao.dto.AlterarStatusRequest;
import _ErrorClub.example.demo.solicitacao.dto.CriarSolicitacaoRequest;
import _ErrorClub.example.demo.solicitacao.entity.Solicitacao;
import _ErrorClub.example.demo.solicitacao.service.SolicitacaoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService service;
    private final CurrentUserService currentUser;

    public SolicitacaoController(SolicitacaoService service, CurrentUserService currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping
    @PreAuthorize("hasRole('ALUNO')")
    public Solicitacao criar(@RequestBody CriarSolicitacaoRequest req) {
        return service.criar(req, currentUser.getCurrentUserId());
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('ALUNO')")
    public List<Solicitacao> minhas() {
        return service.listarMinhas(currentUser.getCurrentUserId());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR','ADMIN')")
    public List<Solicitacao> todas() {
        return service.listarTodas();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROFESSOR','ADMIN')")
    public Solicitacao alterarStatus(@PathVariable UUID id, @RequestBody AlterarStatusRequest req) {
        return service.alterarStatus(id, req, currentUser.getCurrentUserId());
    }
}