import { useState } from "react";
import { useNavigate } from "react-router-dom";

function NovaSolicitacao({ usuario, criarSolicitacao }) {
  const navigate = useNavigate();

  const [tipo, setTipo] = useState("REVISAO_NOTA");
  const [disciplina, setDisciplina] = useState("");
  const [descricao, setDescricao] = useState("");
  const [dataOcorrencia, setDataOcorrencia] = useState("");

  if (usuario.perfil !== "ALUNO") {
    return (
      <div>
        <h1>Acesso negado</h1>
        <p>Apenas alunos podem criar solicitações.</p>
      </div>
    );
  }

  function enviarSolicitacao() {
    if (!disciplina || !descricao || !dataOcorrencia) {
      alert("Preencha todos os campos.");
      return;
    }

    criarSolicitacao({
      tipo,
      disciplina,
      descricao,
      dataOcorrencia,
    });

    navigate("/solicitacoes");
  }

  return (
    <div>
      <h1>Nova solicitação</h1>

      <form>
        <div>
          <label>Tipo da solicitação:</label>
          <br />
          <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
            <option value="REVISAO_NOTA">Revisão de nota</option>
            <option value="ABONO_FALTA">Abono de falta</option>
          </select>
        </div>

        <br />

        <div>
          <label>Disciplina:</label>
          <br />
          <input
            type="text"
            value={disciplina}
            onChange={(e) => setDisciplina(e.target.value)}
            placeholder="Ex: Segurança da Informação"
          />
        </div>

        <br />

        <div>
          <label>Data da ocorrência:</label>
          <br />
          <input
            type="date"
            value={dataOcorrencia}
            onChange={(e) => setDataOcorrencia(e.target.value)}
          />
        </div>

        <br />

        <div>
          <label>Justificativa:</label>
          <br />
          <textarea
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
            placeholder="Descreva o motivo da solicitação"
          />
        </div>

        <br />

        <button type="button" onClick={enviarSolicitacao}>
          Enviar solicitação
        </button>
      </form>
    </div>
  );
}

export default NovaSolicitacao;
