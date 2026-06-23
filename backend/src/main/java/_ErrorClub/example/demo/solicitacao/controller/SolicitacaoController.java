package _ErrorClub.example.demo.solicitacao.controller;

import _ErrorClub.example.demo.infra.security.CurrentUserService;
import _ErrorClub.example.demo.solicitacao.dto.AlterarStatusRequest;
import _ErrorClub.example.demo.solicitacao.dto.CriarSolicitacaoRequest;
import _ErrorClub.example.demo.solicitacao.entity.Solicitacao;
import _ErrorClub.example.demo.solicitacao.service.SolicitacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService service;
    private final CurrentUserService currentUser;

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
    public Solicitacao alterarStatus(@PathVariable UUID id,
                                     @RequestBody AlterarStatusRequest req) {
        return service.alterarStatus(id, req, currentUser.getCurrentUserId());
    }
}
