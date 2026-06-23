package _ErrorClub.example.demo.user.repository;

import _ErrorClub.example.demo.user.entity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
}
