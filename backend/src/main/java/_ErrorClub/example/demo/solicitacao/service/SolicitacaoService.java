package _ErrorClub.example.demo.solicitacao.service;

import _ErrorClub.example.demo.resposta.entity.RespostaSolicitacao;
import _ErrorClub.example.demo.resposta.repository.RespostaSolicitacaoRepository;
import _ErrorClub.example.demo.solicitacao.dto.AlterarStatusRequest;
import _ErrorClub.example.demo.solicitacao.dto.CriarSolicitacaoRequest;
import _ErrorClub.example.demo.solicitacao.entity.Solicitacao;
import _ErrorClub.example.demo.solicitacao.repository.SolicitacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final RespostaSolicitacaoRepository respostaRepository;

    // Como tipo/status são String na entity, validamos na mão p/ não entrar lixo.
    private static final Set<String> TIPOS_VALIDOS = Set.of("REVISAO_NOTA", "ABONO_FALTA");
    private static final Set<String> DECISOES_VALIDAS = Set.of("APROVADA", "REPROVADA");

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              RespostaSolicitacaoRepository respostaRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.respostaRepository = respostaRepository;
    }

    // POST /api/solicitacoes — aluno cria
    public Solicitacao criar(CriarSolicitacaoRequest req, UUID alunoId) {
        if (req.tipo == null || !TIPOS_VALIDOS.contains(req.tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "tipo inválido. Use REVISAO_NOTA ou ABONO_FALTA");
        }
        if (req.descricao == null || req.descricao.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "descricao é obrigatória");
        }

        Solicitacao s = new Solicitacao();
        s.setAlunoId(alunoId);              // vem do usuário logado
        s.setProfessorId(req.professorId);
        s.setDescricao(req.descricao);
        s.setDataOcorrencia(req.dataOcorrencia);
        s.setTipo(req.tipo);
        s.setDisciplina(req.disciplina);
        s.setStatus("PENDENTE");            // toda solicitação nasce pendente
        s.setCreatedAt(OffsetDateTime.now());
        s.setUpdatedAt(OffsetDateTime.now());

        Solicitacao salva = solicitacaoRepository.save(s);
        // TODO (auditoria/Willian): registrar CRIACAO_SOLICITACAO (usuarioId=alunoId, recursoId=salva.getId())
        return salva;
    }

    // GET /api/solicitacoes/minhas — aluno vê só as próprias (dono do recurso)
    public List<Solicitacao> listarMinhas(UUID alunoId) {
        return solicitacaoRepository.findByAlunoId(alunoId);
    }

    // GET /api/solicitacoes — professor/admin veem todas para análise
    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    // PUT /api/solicitacoes/{id}/status — professor aprova/reprova + gera parecer
    public Solicitacao alterarStatus(UUID solicitacaoId, AlterarStatusRequest req, UUID autorId) {
        if (req.status == null || !DECISOES_VALIDAS.contains(req.status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "status inválido. Use APROVADA ou REPROVADA");
        }

        Solicitacao s = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Solicitação não encontrada"));

        // Só decide o que ainda está pendente (evita reprovar algo já aprovado).
        if (!"PENDENTE".equals(s.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação já foi " + s.getStatus());
        }

        s.setStatus(req.status);
        s.setUpdatedAt(OffsetDateTime.now());
        solicitacaoRepository.save(s);

        // Grava o parecer (resposta) junto com a decisão
        RespostaSolicitacao resposta = new RespostaSolicitacao();
        resposta.setSolicitacaoId(s.getId());
        resposta.setAutorId(autorId);
        resposta.setTexto(req.textoParecer);
        resposta.setDecisao(req.status);
        resposta.setCreatedAt(OffsetDateTime.now());
        respostaRepository.save(resposta);

        // TODO (auditoria/Willian): registrar ALTERACAO_STATUS + (APROVACAO_SOLICITACAO ou REPROVACAO_SOLICITACAO)
        return s;
    }
}