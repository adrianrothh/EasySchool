package _ErrorClub.example.demo.service;

import _ErrorClub.example.demo.entity.AuditLog;
import _ErrorClub.example.demo.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repository;

    public void registrar(String evento, String detalhe) {

        AuditLog log = new AuditLog();

        log.setEvento(evento);
        log.setDetalhe(detalhe);
        log.setCreatedAt(OffsetDateTime.now());

        repository.save(log);
    }
}