import { logsMock } from "../data/mockData";

function Logs({ usuario }) {
  if (usuario.perfil !== "ADMIN") {
    return (
      <div>
        <h1>Acesso negado</h1>
        <p>Apenas administradores podem visualizar logs.</p>
      </div>
    );
  }

  return (
    <div>
      <h1>Logs de auditoria</h1>

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>ID</th>
            <th>Usuário</th>
            <th>Ação</th>
            <th>Data</th>
          </tr>
        </thead>

        <tbody>
          {logsMock.map((log) => (
            <tr key={log.id}>
              <td>{log.id}</td>
              <td>{log.usuario}</td>
              <td>{log.acao}</td>
              <td>{log.data}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Logs;
