package _ErrorClub.example.demo.solicitacao.service;

import _ErrorClub.example.demo.audit.model.AuditEvento;
import _ErrorClub.example.demo.audit.service.AuditLogService;
import _ErrorClub.example.demo.solicitacao.dto.AlterarStatusRequest;
import _ErrorClub.example.demo.solicitacao.dto.CriarSolicitacaoRequest;
import _ErrorClub.example.demo.solicitacao.model.RespostaSolicitacao;
import _ErrorClub.example.demo.solicitacao.model.Solicitacao;
import _ErrorClub.example.demo.solicitacao.model.StatusSolicitacao;
import _ErrorClub.example.demo.solicitacao.repository.RespostaSolicitacaoRepository;
import _ErrorClub.example.demo.solicitacao.repository.SolicitacaoRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private static final String RECURSO_TIPO = "SOLICITACAO";

    private final SolicitacaoRepository solicitacaoRepository;
    private final RespostaSolicitacaoRepository respostaRepository;
    private final AuditLogService auditLogService;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              RespostaSolicitacaoRepository respostaRepository,
                              AuditLogService auditLogService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.respostaRepository = respostaRepository;
        this.auditLogService = auditLogService;
    }

    public Solicitacao criar(CriarSolicitacaoRequest req, UUID alunoId) {
        validarCriacao(req, alunoId);

        OffsetDateTime now = OffsetDateTime.now();

        Solicitacao s = new Solicitacao();
        s.setAlunoId(alunoId);
        s.setProfessorId(req.getProfessorId());
        s.setDescricao(req.getDescricao());
        s.setDataOcorrencia(req.getDataOcorrencia());
        s.setTipo(req.getTipo());
        s.setDisciplina(req.getDisciplina());
        s.setStatus(StatusSolicitacao.PENDENTE);
        s.setCreatedAt(now);
        s.setUpdatedAt(now);

        Solicitacao salva = solicitacaoRepository.save(s);

        auditar(
            AuditEvento.SOLICITACAO_CRIADA,
            "Tipo=" + salva.getTipo() + ", disciplina=" + salva.getDisciplina(),
            alunoId,
            salva.getId()
        );

        return salva;
    }

    public List<Solicitacao> listarMinhas(UUID alunoId) {
        List<Solicitacao> lista = solicitacaoRepository.findByAlunoId(alunoId);

        auditar(
            AuditEvento.SOLICITACAO_LISTADA_MINHAS,
            "Total=" + lista.size(),
            alunoId,
            null
        );

        return lista;
    }

    public List<Solicitacao> listarTodas() {
        List<Solicitacao> lista = solicitacaoRepository.findAll();

        auditar(
            AuditEvento.SOLICITACAO_LISTADA_TODAS,
            "Total=" + lista.size(),
            null,
            null
        );

        return lista;
    }

    public Solicitacao alterarStatus(UUID solicitacaoId, AlterarStatusRequest req, UUID autorId) {
        StatusSolicitacao novoStatus = validarDecisao(req.getStatus(), autorId, solicitacaoId);
        Solicitacao s = buscarPendente(solicitacaoId, autorId);

        StatusSolicitacao statusAnterior = s.getStatus();
        OffsetDateTime now = OffsetDateTime.now();

        s.setStatus(novoStatus);
        s.setUpdatedAt(now);

        Solicitacao salva = solicitacaoRepository.save(s);

        registrarResposta(
            salva.getId(),
            autorId,
            req.getTextoParecer(),
            novoStatus,
            now
        );

        auditar(
            AuditEvento.SOLICITACAO_STATUS_ALTERADO,
            "De " + statusAnterior + " para " + novoStatus,
            autorId,
            salva.getId()
        );

        return salva;
    }

    private void validarCriacao(CriarSolicitacaoRequest req, UUID alunoId) {
        if (req.getTipo() == null) {
            auditar(AuditEvento.SOLICITACAO_CRIAR_FAIL, "Tipo inválido: null", alunoId, null);
            throw badRequest("tipo inválido. Use REVISAO_NOTA ou ABONO_FALTA");
        }

        if (req.getDescricao() == null || req.getDescricao().isBlank()) {
            auditar(AuditEvento.SOLICITACAO_CRIAR_FAIL, "Descricao vazia", alunoId, null);
            throw badRequest("descricao é obrigatória");
        }
    }

    private StatusSolicitacao validarDecisao(StatusSolicitacao status, UUID autorId, UUID solicitacaoId) {
        if (status == null || status == StatusSolicitacao.PENDENTE) {
            auditar(
                AuditEvento.SOLICITACAO_STATUS_FAIL,
                "Status inválido: " + status,
                autorId,
                solicitacaoId
            );

            throw badRequest("status inválido. Use APROVADA ou REPROVADA");
        }

        return status;
    }

    private Solicitacao buscarPendente(UUID solicitacaoId, UUID autorId) {
        return solicitacaoRepository.findById(solicitacaoId)
            .orElseThrow(() -> {
                auditar(
                    AuditEvento.SOLICITACAO_STATUS_FAIL,
                    "Solicitação não encontrada",
                    autorId,
                    solicitacaoId
                );

                return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Solicitação não encontrada"
                );
            });
    }

    private void registrarResposta(UUID solicitacaoId,
                                   UUID autorId,
                                   String texto,
                                   StatusSolicitacao decisao,
                                   OffsetDateTime now) {
        RespostaSolicitacao resposta = new RespostaSolicitacao();
        resposta.setSolicitacaoId(solicitacaoId);
        resposta.setAutorId(autorId);
        resposta.setTexto(texto);
        resposta.setDecisao(decisao);
        resposta.setCreatedAt(now);

        respostaRepository.save(resposta);
    }

    private void auditar(AuditEvento evento, String detalhe, UUID usuarioId, UUID recursoId) {
        auditLogService.registrar(
            evento,
            detalhe,
            usuarioId,
            recursoId == null ? null : recursoId.toString(),
            RECURSO_TIPO
        );
    }

    private ResponseStatusException badRequest(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
