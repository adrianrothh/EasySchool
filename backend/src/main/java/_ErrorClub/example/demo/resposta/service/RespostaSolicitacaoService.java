package _ErrorClub.example.demo.resposta.service;

import _ErrorClub.example.demo.resposta.entity.RespostaSolicitacao;
import _ErrorClub.example.demo.resposta.repository.RespostaSolicitacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RespostaSolicitacaoService {

    private final RespostaSolicitacaoRepository respostaRepository;

    public RespostaSolicitacaoService(RespostaSolicitacaoRepository respostaRepository) {
        this.respostaRepository = respostaRepository;
    }

    public List<RespostaSolicitacao> listarPorSolicitacao(UUID solicitacaoId) {
        return respostaRepository.findBySolicitacaoId(solicitacaoId);
    }
}