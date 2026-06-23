package _ErrorClub.example.demo.resposta.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Respostas dos professores às solicitações dos alunos.
 * Restrito a ROLE_PROFESSOR e ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/respostas")
public class RespostaSolicitacaoController {

}
