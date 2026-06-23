package _ErrorClub.example.demo.audit.repository;

import _ErrorClub.example.demo.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
