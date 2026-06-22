import { Link } from "react-router-dom";

function Layout({ usuario, logout, children }) {
  return (
    <div>
      <header>
        <h2>EasySchool</h2>

        <p>
          Usuário: {usuario.nome} | Perfil: {usuario.perfil}
        </p>

        <nav>
          <Link to="/dashboard">Dashboard</Link>
          {" | "}

          {usuario.perfil === "ALUNO" && (
            <>
              <Link to="/solicitacoes/nova">Nova solicitação</Link>
              {" | "}
              <Link to="/solicitacoes">Minhas solicitações</Link>
              {" | "}
            </>
          )}

          {usuario.perfil === "PROFESSOR" && (
            <>
              <Link to="/solicitacoes">Solicitações para análise</Link>
              {" | "}
            </>
          )}

          {usuario.perfil === "ADMIN" && (
            <>
              <Link to="/solicitacoes">Solicitações</Link>
              {" | "}
              <Link to="/logs">Logs</Link>
              {" | "}
            </>
          )}

          <button onClick={logout}>Sair</button>
        </nav>

        <hr />
      </header>

      <main>{children}</main>
    </div>
  );
}

export default Layout;
