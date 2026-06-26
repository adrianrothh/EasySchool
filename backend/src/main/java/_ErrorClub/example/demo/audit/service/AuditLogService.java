package _ErrorClub.example.demo.audit.service;

import _ErrorClub.example.demo.audit.entity.AuditLog;
import _ErrorClub.example.demo.audit.enums.AuditEvento;
import _ErrorClub.example.demo.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void registrar(AuditEvento evento, String detalhe) {
        registrar(evento, detalhe, null, null, null);
    }

    public void registrar(AuditEvento evento,
                          String detalhe,
                          UUID usuarioId,
                          String recursoId,
                          String recursoTipo) {

        AuditLog log = new AuditLog();
        log.setEvento(evento.name());
        log.setDetalhe(detalhe);
        log.setUsuarioId(usuarioId);
        log.setRecursoTipo(recursoTipo);
        log.setUserAgent(extrairUserAgent());
        log.setCreatedAt(OffsetDateTime.now());

        repository.save(log);
    }

    private String extrairUserAgent() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest().getHeader("User-Agent");
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
