package _ErrorClub.example.demo.repository;

import _ErrorClub.example.demo.entity.Parecer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParecerRepository extends JpaRepository<Parecer, Long> {
}