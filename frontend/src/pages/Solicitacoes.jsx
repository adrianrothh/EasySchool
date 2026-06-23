function Solicitacoes({ usuario, solicitacoes = [], alterarStatus }) {
  const lista =
    usuario.perfil === "ALUNO"
      ? solicitacoes.filter((item) => item.alunoEmail === usuario.email)
      : solicitacoes;

  return (
    <div>
      <h1>
        {usuario.perfil === "ALUNO"
          ? "Minhas solicitações"
          : "Solicitações para análise"}
      </h1>

      {lista.length === 0 ? (
        <p>Nenhuma solicitação encontrada.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tipo</th>
              <th>Disciplina</th>
              <th>Aluno</th>
              <th>Data</th>
              <th>Status</th>
              {usuario.perfil !== "ALUNO" && <th>Ações</th>}
            </tr>
          </thead>

          <tbody>
            {lista.map((solicitacao) => (
              <tr key={solicitacao.id}>
                <td>{solicitacao.id}</td>
                <td>{solicitacao.tipo}</td>
                <td>{solicitacao.disciplina}</td>
                <td>{solicitacao.alunoNome}</td>
                <td>{solicitacao.dataOcorrencia}</td>
                <td>{solicitacao.status}</td>

                {usuario.perfil !== "ALUNO" && (
                  <td>
                    <button
                      onClick={() => alterarStatus(solicitacao.id, "APROVADA")}
                    >
                      Aprovar
                    </button>{" "}
                    <button
                      onClick={() => alterarStatus(solicitacao.id, "REPROVADA")}
                    >
                      Reprovar
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Solicitacoes;
