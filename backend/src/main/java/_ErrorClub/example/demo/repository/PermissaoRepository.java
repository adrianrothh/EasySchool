package _ErrorClub.example.demo.repository;

import _ErrorClub.example.demo.entity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
}