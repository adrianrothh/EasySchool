import { useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";

import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import NovaSolicitacao from "./pages/NovaSolicitacao";
import Solicitacoes from "./pages/Solicitacoes";
import Logs from "./pages/Logs";

function App() {
  const navigate = useNavigate();

  const [usuario, setUsuario] = useState(() => {
    const usuarioSalvo = localStorage.getItem("usuario");
    return usuarioSalvo ? JSON.parse(usuarioSalvo) : null;
  });

  function login(perfil) {
    const dadosUsuario = {
      nome:
        perfil === "ALUNO"
          ? "Aluno Teste"
          : perfil === "PROFESSOR"
            ? "Professor Teste"
            : "Administrador Teste",
      email:
        perfil === "ALUNO"
          ? "aluno@easyschool.com"
          : perfil === "PROFESSOR"
            ? "professor@easyschool.com"
            : "admin@easyschool.com",
      perfil,
    };

    localStorage.setItem("usuario", JSON.stringify(dadosUsuario));
    setUsuario(dadosUsuario);
    navigate("/dashboard");
  }

  function logout() {
    localStorage.removeItem("usuario");
    setUsuario(null);
    navigate("/login");
  }

  function protegerPagina(pagina) {
    if (!usuario) {
      return <Navigate to="/login" />;
    }

    return (
      <Layout usuario={usuario} logout={logout}>
        {pagina}
      </Layout>
    );
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={
          usuario ? <Navigate to="/dashboard" /> : <Login login={login} />
        }
      />

      <Route
        path="/dashboard"
        element={protegerPagina(<Dashboard usuario={usuario} />)}
      />

      <Route
        path="/solicitacoes"
        element={protegerPagina(<Solicitacoes usuario={usuario} />)}
      />

      <Route
        path="/solicitacoes/nova"
        element={protegerPagina(<NovaSolicitacao usuario={usuario} />)}
      />

      <Route
        path="/logs"
        element={protegerPagina(<Logs usuario={usuario} />)}
      />

      <Route
        path="*"
        element={<Navigate to={usuario ? "/dashboard" : "/login"} />}
      />
    </Routes>
  );
}

export default App;
