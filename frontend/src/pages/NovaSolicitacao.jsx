function NovaSolicitacao({ usuario }) {
  if (usuario.perfil !== "ALUNO") {
    return (
      <div>
        <h1>Acesso negado</h1>
        <p>Apenas alunos podem criar solicitações.</p>
      </div>
    );
  }

  return (
    <div>
      <h1>Nova solicitação</h1>

      <form>
        <div>
          <label>Tipo da solicitação:</label>
          <br />
          <select>
            <option>Revisão de nota</option>
            <option>Abono de falta</option>
          </select>
        </div>

        <br />

        <div>
          <label>Disciplina:</label>
          <br />
          <input type="text" placeholder="Ex: Segurança da Informação" />
        </div>

        <br />

        <div>
          <label>Justificativa:</label>
          <br />
          <textarea placeholder="Descreva o motivo da solicitação"></textarea>
        </div>

        <br />

        <button type="button">Enviar solicitação</button>
      </form>
    </div>
  );
}

export default NovaSolicitacao;
