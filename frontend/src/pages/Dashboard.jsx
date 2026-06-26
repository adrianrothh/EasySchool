function Dashboard({ usuario }) {
  return (
    <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-6">
      <h1 className="text-2xl font-bold text-slate-900">Dashboard</h1>

      <p className="mt-2 text-slate-500">
        Bem-vindo ao EasySchool,{" "}
        <span className="font-semibold text-slate-800">{usuario.nome}</span>.
      </p>

      <div className="mt-5 rounded-lg bg-slate-50 border border-slate-200 p-4">
        <p className="text-sm text-slate-600">
          Perfil atual:{" "}
          <span className="font-semibold text-blue-600">{usuario.perfil}</span>
        </p>

        {usuario.perfil === "ALUNO" && (
          <p className="mt-2 text-sm text-slate-500">
            Você pode criar solicitações acadêmicas e acompanhar o andamento dos
            seus pedidos.
          </p>
        )}

        {usuario.perfil === "PROFESSOR" && (
          <p className="mt-2 text-sm text-slate-500">
            Você pode visualizar solicitações dos alunos e aprovar ou reprovar
            os pedidos.
          </p>
        )}

        {usuario.perfil === "ADMIN" && (
          <p className="mt-2 text-sm text-slate-500">
            Você pode acompanhar as solicitações e visualizar os logs de
            auditoria do sistema.
          </p>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
