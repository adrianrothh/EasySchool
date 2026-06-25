import { Link } from "react-router-dom";

function Layout({ usuario, logout, children }) {
  return (
    <div className="min-h-screen bg-slate-100">
      <header className="bg-white border-b border-slate-200 shadow-sm">
        <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between gap-4">
          <div>
            <h1 className="text-xl font-bold text-slate-900">EasySchool</h1>
            <p className="text-sm text-slate-500">
              {usuario.nome} ·{" "}
              <span className="font-semibold text-blue-600">
                {usuario.perfil}
              </span>
            </p>
          </div>

          <nav className="flex items-center gap-2 flex-wrap justify-end">
            <Link
              to="/dashboard"
              className="px-3 py-2 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100"
            >
              Dashboard
            </Link>

            {usuario.perfil === "ALUNO" && (
              <>
                <Link
                  to="/solicitacoes/nova"
                  className="px-3 py-2 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100"
                >
                  Nova solicitação
                </Link>

                <Link
                  to="/solicitacoes"
                  className="px-3 py-2 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100"
                >
                  Minhas solicitações
                </Link>
              </>
            )}

            {usuario.perfil === "PROFESSOR" && (
              <Link
                to="/solicitacoes"
                className="px-3 py-2 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100"
              >
                Solicitações
              </Link>
            )}

            {usuario.perfil === "ADMIN" && (
              <>
                <Link
                  to="/solicitacoes"
                  className="px-3 py-2 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100"
                >
                  Solicitações
                </Link>

                <Link
                  to="/logs"
                  className="px-3 py-2 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100"
                >
                  Logs
                </Link>
              </>
            )}

            <button
              onClick={logout}
              className="px-3 py-2 rounded-lg text-sm font-semibold text-white bg-slate-800 hover:bg-slate-900"
            >
              Sair
            </button>
          </nav>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-8">{children}</main>
    </div>
  );
}

export default Layout;
