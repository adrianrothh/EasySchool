package _ErrorClub.example.demo.audit.controller;

import _ErrorClub.example.demo.audit.entity.AuditLog;
import _ErrorClub.example.demo.audit.repository.AuditLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}