function Logs({ usuario, logs }) {
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

      {logs.length === 0 ? (
        <p>Nenhum log registrado.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>ID</th>
              <th>Usuário</th>
              <th>Evento</th>
              <th>Recurso</th>
              <th>Detalhe</th>
              <th>Data</th>
            </tr>
          </thead>

          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td>{log.id}</td>
                <td>{log.usuario}</td>
                <td>{log.evento}</td>
                <td>{log.recursoTipo}</td>
                <td>{log.detalhe}</td>
                <td>{log.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Logs;
