function Dashboard({ usuario }) {
  return (
    <div>
      <h1>Dashboard</h1>

      <p>Bem-vindo ao EasySchool.</p>

      <p>
        Nome: <strong>{usuario.nome}</strong>
      </p>

      <p>
        Email: <strong>{usuario.email}</strong>
      </p>

      <p>
        Perfil: <strong>{usuario.perfil}</strong>
      </p>
    </div>
  );
}

export default Dashboard;
