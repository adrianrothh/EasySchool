package _ErrorClub.example.demo.solicitacao.controller;

import _ErrorClub.example.demo.auth.service.CustomUserDetails;
import _ErrorClub.example.demo.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal CustomUserDetails principal) {
        //User user = principal.getUser();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
