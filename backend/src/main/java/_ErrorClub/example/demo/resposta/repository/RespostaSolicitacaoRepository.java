package _ErrorClub.example.demo.resposta.repository;

import _ErrorClub.example.demo.resposta.entity.RespostaSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RespostaSolicitacaoRepository extends JpaRepository<RespostaSolicitacao, UUID> {

    List<RespostaSolicitacao> findBySolicitacaoId(UUID solicitacaoId);
}
