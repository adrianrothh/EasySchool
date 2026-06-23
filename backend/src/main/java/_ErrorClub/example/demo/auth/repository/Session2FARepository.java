package _ErrorClub.example.demo.auth.repository;

import _ErrorClub.example.demo.auth.entity.Session2FA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Session2FARepository extends JpaRepository<Session2FA, Long> {
}
