package _ErrorClub.example.demo.repository;

import _ErrorClub.example.demo.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}