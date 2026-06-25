import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { criarSolicitacao } from "../services/api";

const disciplinas = [
  "Segurança da Informação",
  "Engenharia de Software",
  "Banco de Dados",
  "Programação Web",
  "Sistemas Operacionais",
];

const professorPorDisciplina = {
  "Segurança da Informação": "",
  "Engenharia de Software": "",
  "Banco de Dados": "",
  "Programação Web": "",
  "Sistemas Operacionais": "",
};

function dataHoje() {
  return new Date().toISOString().split("T")[0];
}

function NovaSolicitacao({ usuario }) {
  const navigate = useNavigate();

  const [tipo, setTipo] = useState("REVISAO_NOTA");
  const [disciplina, setDisciplina] = useState(disciplinas[0]);
  const [dataOcorrencia, setDataOcorrencia] = useState("");
  const [descricao, setDescricao] = useState("");

  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  if (usuario.perfil !== "ALUNO") {
    return (
      <div className="bg-white border border-red-200 rounded-xl shadow-sm p-6">
        <h1 className="text-2xl font-bold text-red-700">Acesso negado</h1>
        <p className="mt-2 text-sm text-slate-500">
          Apenas alunos podem criar solicitações acadêmicas.
        </p>
      </div>
    );
  }

  async function enviar(event) {
    event.preventDefault();

    setErro("");
    setCarregando(true);

    if (dataOcorrencia > dataHoje()) {
      setErro("A data da ocorrência não pode ser no futuro.");
      setCarregando(false);
      return;
    }

    try {
      const dados = {
        descricao,
        dataOcorrencia,
        tipo,
        disciplina,
      };

      const professorId = professorPorDisciplina[disciplina];

      if (professorId) {
        dados.professorId = professorId;
      }

      await criarSolicitacao(dados);
      navigate("/solicitacoes");
    } catch (error) {
      setErro(error.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-6 text-left">
      <div className="mb-6 text-left">
        <h1 className="text-2xl font-bold text-slate-900">Nova solicitação</h1>
        <p className="mt-2 text-sm text-slate-500">
          Abra uma solicitação de revisão de nota ou abono de falta.
        </p>
      </div>

      <form onSubmit={enviar} className="space-y-5 max-w-xl">
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Tipo
          </label>

          <select
            value={tipo}
            onChange={(event) => setTipo(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            <option value="REVISAO_NOTA">Revisão de nota</option>
            <option value="ABONO_FALTA">Abono de falta</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Disciplina
          </label>

          <select
            value={disciplina}
            onChange={(event) => setDisciplina(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            {disciplinas.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Data da ocorrência
          </label>

          <input
            type="date"
            value={dataOcorrencia}
            max={dataHoje()}
            onChange={(event) => setDataOcorrencia(event.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            required
          />

          <p className="mt-1 text-xs text-slate-400">
            A data não pode ser no futuro.
          </p>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Descrição
          </label>

          <textarea
            value={descricao}
            onChange={(event) => setDescricao(event.target.value)}
            placeholder="Descreva o motivo da solicitação"
            className="w-full min-h-28 rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none resize-y focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
            required
          />
        </div>

        {erro && (
          <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {erro}
          </div>
        )}

        <div className="flex items-center justify-start gap-3 pt-1">
          <button
            type="submit"
            disabled={carregando}
            className="rounded-lg bg-blue-600 text-white px-4 py-2 text-sm font-semibold hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed transition"
          >
            {carregando ? "Enviando..." : "Enviar solicitação"}
          </button>

          <button
            type="button"
            onClick={() => navigate("/solicitacoes")}
            className="rounded-lg border border-slate-300 text-slate-700 px-4 py-2 text-sm font-medium hover:bg-slate-50 transition"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}

export default NovaSolicitacao;
