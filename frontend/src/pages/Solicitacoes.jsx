import { solicitacoesMock } from "../data/mockData";

function Solicitacoes({ usuario }) {
  return (
    <div>
      <h1>
        {usuario.perfil === "ALUNO"
          ? "Minhas solicitações"
          : "Solicitações para análise"}
      </h1>

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tipo</th>
            <th>Disciplina</th>
            <th>Aluno</th>
            <th>Status</th>
            {usuario.perfil !== "ALUNO" && <th>Ações</th>}
          </tr>
        </thead>

        <tbody>
          {solicitacoesMock.map((solicitacao) => (
            <tr key={solicitacao.id}>
              <td>{solicitacao.id}</td>
              <td>{solicitacao.tipo}</td>
              <td>{solicitacao.disciplina}</td>
              <td>{solicitacao.aluno}</td>
              <td>{solicitacao.status}</td>

              {usuario.perfil !== "ALUNO" && (
                <td>
                  <button>Aprovar</button> <button>Reprovar</button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Solicitacoes;
