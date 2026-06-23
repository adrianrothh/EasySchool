package _ErrorClub.example.demo.audit.repository;

import _ErrorClub.example.demo.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}