package _ErrorClub.example.demo.repository;

import _ErrorClub.example.demo.entity.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
}