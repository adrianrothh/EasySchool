import { useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";

import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import NovaSolicitacao from "./pages/NovaSolicitacao";
import Solicitacoes from "./pages/Solicitacoes";
import Logs from "./pages/Logs";

function descobrirPerfilPeloEmail(email) {
  const emailLower = email.toLowerCase();

  if (emailLower.includes("aluno")) return "ALUNO";
  if (emailLower.includes("professor")) return "PROFESSOR";
  if (emailLower.includes("admin")) return "ADMIN";

  return "ALUNO";
}

function descobrirNomePeloPerfil(perfil) {
  if (perfil === "ALUNO") return "Aluno Teste";
  if (perfil === "PROFESSOR") return "Professor Teste";
  if (perfil === "ADMIN") return "Administrador Teste";

  return "Usuário";
}

function App() {
  const navigate = useNavigate();

  const [usuario, setUsuario] = useState(() => {
    const usuarioSalvo = localStorage.getItem("usuario");
    return usuarioSalvo ? JSON.parse(usuarioSalvo) : null;
  });

  function salvarSessao(email, tokens) {
    const perfil = descobrirPerfilPeloEmail(email);

    const usuarioLogado = {
      nome: descobrirNomePeloPerfil(perfil),
      email,
      perfil,
    };

    localStorage.setItem("accessToken", tokens.accessToken);
    localStorage.setItem("refreshToken", tokens.refreshToken);
    localStorage.setItem("usuario", JSON.stringify(usuarioLogado));

    setUsuario(usuarioLogado);
    navigate("/dashboard");
  }

  function logout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
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
          usuario ? (
            <Navigate to="/dashboard" />
          ) : (
            <Login salvarSessao={salvarSessao} />
          )
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
