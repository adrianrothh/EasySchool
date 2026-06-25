import { useEffect, useState } from "react";
import { buscarLogs } from "../services/api";

function formatarData(data) {
  if (!data) return "-";

  return new Date(data).toLocaleString("pt-BR");
}

function Logs({ usuario }) {
  const [logs, setLogs] = useState([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    async function carregarLogs() {
      setErro("");
      setCarregando(true);

      try {
        const dados = await buscarLogs();
        setLogs(dados);
      } catch (error) {
        setErro(error.message);
      } finally {
        setCarregando(false);
      }
    }

    carregarLogs();
  }, []);

  if (usuario.perfil !== "ADMIN") {
    return (
      <div className="bg-white border border-red-200 rounded-xl shadow-sm p-6 text-left">
        <h1 className="text-2xl font-bold text-red-700">Acesso negado</h1>
        <p className="mt-2 text-sm text-slate-500">
          Apenas administradores podem visualizar os logs de auditoria.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-6 text-left">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Logs de auditoria</h1>

        <p className="mt-2 text-sm text-slate-500">
          Registros de ações realizadas no sistema.
        </p>
      </div>

      {carregando && (
        <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600">
          Carregando logs...
        </div>
      )}

      {erro && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {erro}
        </div>
      )}

      {!carregando && logs.length === 0 && (
        <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-6 text-center text-sm text-slate-500">
          Nenhum log encontrado.
        </div>
      )}

      {!carregando && logs.length > 0 && (
        <div className="overflow-x-auto rounded-xl border border-slate-200">
          <table className="w-full min-w-[900px] border-collapse bg-white">
            <thead className="bg-slate-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Evento
                </th>

                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Usuário
                </th>

                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Recurso
                </th>

                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Detalhe
                </th>

                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Data
                </th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-200">
              {logs.map((log) => (
                <tr key={log.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3">
                    <span className="inline-flex rounded-full border border-blue-200 bg-blue-50 px-2.5 py-1 text-xs font-semibold text-blue-700">
                      {log.evento || "-"}
                    </span>
                  </td>

                  <td className="px-4 py-3 text-xs text-slate-500 max-w-[180px] break-all">
                    {log.usuarioId || "-"}
                  </td>

                  <td className="px-4 py-3 text-sm text-slate-700">
                    {log.recursoTipo || "-"}
                  </td>

                  <td className="px-4 py-3 text-sm text-slate-700 max-w-[320px]">
                    {log.detalhe || "-"}
                  </td>

                  <td className="px-4 py-3 text-sm text-slate-700 whitespace-nowrap">
                    {formatarData(log.createdAt)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default Logs;
