package _ErrorClub.example.demo.service;

import _ErrorClub.example.demo.entity.AuditLog;
import _ErrorClub.example.demo.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void registrar(String evento, String detalhe) {

        AuditLog log = new AuditLog();

        log.setEvento(evento);
        log.setDetalhe(detalhe);
        log.setCreatedAt(OffsetDateTime.now());

        repository.save(log);
    }
}