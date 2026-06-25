package _ErrorClub.example.demo.solicitacao.repository;

import _ErrorClub.example.demo.solicitacao.entity.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {

    List<Solicitacao> findByAlunoId(UUID alunoId);
}
