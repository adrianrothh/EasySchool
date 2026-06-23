package _ErrorClub.example.demo.user.repository;

import _ErrorClub.example.demo.user.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository extends JpaRepository<Perfil, UUID> {
    Optional<Perfil> findByNome(String nome);
}
