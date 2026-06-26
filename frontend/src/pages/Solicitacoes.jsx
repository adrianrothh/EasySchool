import { useEffect, useState } from "react";
import {
  alterarStatusSolicitacao,
  buscarMinhasSolicitacoes,
  buscarTodasSolicitacoes,
} from "../services/api";

function ordenarSolicitacoes(lista) {
  return [...lista].sort((a, b) => {
    const dataA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
    const dataB = b.createdAt ? new Date(b.createdAt).getTime() : 0;

    if (dataA !== dataB) {
      return dataA - dataB;
    }

    return String(a.id).localeCompare(String(b.id));
  });
}

function formatarTipo(tipo) {
  if (tipo === "REVISAO_NOTA") return "Revisão de nota";
  if (tipo === "ABONO_FALTA") return "Abono de falta";
  return tipo || "-";
}

function statusClasses(status) {
  if (status === "APROVADA") {
    return "bg-green-50 text-green-700 border-green-200";
  }

  if (status === "REPROVADA") {
    return "bg-red-50 text-red-700 border-red-200";
  }

  return "bg-amber-50 text-amber-700 border-amber-200";
}

function Solicitacoes({ usuario }) {
  const [solicitacoes, setSolicitacoes] = useState([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [alterandoId, setAlterandoId] = useState(null);

  async function carregarSolicitacoes() {
    setErro("");
    setCarregando(true);

    try {
      const dados =
        usuario.perfil === "ALUNO"
          ? await buscarMinhasSolicitacoes()
          : await buscarTodasSolicitacoes();

      setSolicitacoes(ordenarSolicitacoes(dados));
    } catch (error) {
      setErro(error.message);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarSolicitacoes();
  }, []);

  async function alterarStatus(id, status) {
    const confirmar = window.confirm(
      status === "APROVADA"
        ? "Tem certeza que deseja aprovar esta solicitação?"
        : "Tem certeza que deseja reprovar esta solicitação?",
    );

    if (!confirmar) {
      return;
    }

    setErro("");
    setAlterandoId(id);

    try {
      const solicitacaoAtualizada = await alterarStatusSolicitacao(id, {
        status,
        textoParecer:
          status === "APROVADA"
            ? "Solicitação aprovada."
            : "Solicitação reprovada.",
      });

      setSolicitacoes((listaAtual) =>
        listaAtual.map((solicitacao) =>
          solicitacao.id === id ? solicitacaoAtualizada : solicitacao,
        ),
      );
    } catch (error) {
      setErro(error.message);
    } finally {
      setAlterandoId(null);
    }
  }

  return (
    <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">
          {usuario.perfil === "ALUNO"
            ? "Minhas solicitações"
            : "Solicitações para análise"}
        </h1>

        <p className="mt-2 text-sm text-slate-500">
          {usuario.perfil === "ALUNO"
            ? "Acompanhe o status das solicitações criadas por você."
            : "Visualize as solicitações e altere o status quando necessário."}
        </p>
      </div>

      {carregando && (
        <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600">
          Carregando solicitações...
        </div>
      )}

      {erro && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {erro}
        </div>
      )}

      {!carregando && solicitacoes.length === 0 && (
        <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-6 text-center text-sm text-slate-500">
          Nenhuma solicitação encontrada.
        </div>
      )}

      {!carregando && solicitacoes.length > 0 && (
        <div className="overflow-x-auto rounded-xl border border-slate-200">
          <table className="w-full min-w-[850px] border-collapse bg-white">
            <thead className="bg-slate-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  ID
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Tipo
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Disciplina
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Descrição
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Data
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Status
                </th>
                {usuario.perfil !== "ALUNO" && (
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    Ações
                  </th>
                )}
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-200">
              {solicitacoes.map((solicitacao) => (
                <tr key={solicitacao.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3 text-xs text-slate-500 max-w-[150px] break-all">
                    {solicitacao.id}
                  </td>

                  <td className="px-4 py-3 text-sm text-slate-700">
                    {formatarTipo(solicitacao.tipo)}
                  </td>

                  <td className="px-4 py-3 text-sm text-slate-700">
                    {solicitacao.disciplina || "-"}
                  </td>

                  <td className="px-4 py-3 text-sm text-slate-700 max-w-[280px]">
                    {solicitacao.descricao || "-"}
                  </td>

                  <td className="px-4 py-3 text-sm text-slate-700">
                    {solicitacao.dataOcorrencia || "-"}
                  </td>

                  <td className="px-4 py-3">
                    <span
                      className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-semibold ${statusClasses(
                        solicitacao.status,
                      )}`}
                    >
                      {solicitacao.status || "PENDENTE"}
                    </span>
                  </td>

                  {usuario.perfil !== "ALUNO" && (
                    <td className="px-4 py-3">
                      <div className="flex flex-wrap gap-2">
                        <button
                          disabled={alterandoId === solicitacao.id}
                          onClick={() =>
                            alterarStatus(solicitacao.id, "APROVADA")
                          }
                          className="rounded-lg bg-green-600 px-3 py-1.5 text-xs font-semibold text-white hover:bg-green-700 disabled:opacity-60 disabled:cursor-not-allowed"
                        >
                          Aprovar
                        </button>

                        <button
                          disabled={alterandoId === solicitacao.id}
                          onClick={() =>
                            alterarStatus(solicitacao.id, "REPROVADA")
                          }
                          className="rounded-lg bg-red-600 px-3 py-1.5 text-xs font-semibold text-white hover:bg-red-700 disabled:opacity-60 disabled:cursor-not-allowed"
                        >
                          Reprovar
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default Solicitacoes;
