package _ErrorClub.example.demo.repository;

import _ErrorClub.example.demo.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByNome(String nome);
}