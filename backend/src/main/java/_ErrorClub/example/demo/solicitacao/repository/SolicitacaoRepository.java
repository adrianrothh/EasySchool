package _ErrorClub.example.demo.solicitacao.repository;

import _ErrorClub.example.demo.solicitacao.entity.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {

    List<Solicitacao> findByAlunoId(UUID alunoId);
}
