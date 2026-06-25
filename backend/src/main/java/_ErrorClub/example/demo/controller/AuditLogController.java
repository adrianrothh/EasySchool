package _ErrorClub.example.demo.controller;

import _ErrorClub.example.demo.audit.entity.AuditLog;
import _ErrorClub.example.demo.audit.repository.AuditLogRepository;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogRepository repository;

    public AuditLogController(AuditLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AuditLog> listar() {
        return repository.findAll();
    }

    @GetMapping("/teste")
    public String criarLogTeste() {

        AuditLog log = new AuditLog();

        log.setEvento("TESTE_LOG");
        log.setDetalhe("Log criado manualmente");
        log.setCreatedAt(OffsetDateTime.now());

        repository.save(log);

        return "Log criado com sucesso!";
    }
}