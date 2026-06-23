package _ErrorClub.example.demo.controller;

import _ErrorClub.example.demo.entity.AuditLog;
import _ErrorClub.example.demo.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository repository;

    @GetMapping
    public List<AuditLog> listar() {
        return repository.findAll();
    }
}