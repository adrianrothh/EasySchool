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

  const [solicitacoes, setSolicitacoes] = useState(() => {
    const dados = localStorage.getItem("solicitacoes");
    return dados ? JSON.parse(dados) : [];
  });

  const [logs, setLogs] = useState(() => {
    const dados = localStorage.getItem("logs");
    return dados ? JSON.parse(dados) : [];
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

  function registrarLog(evento, detalhe, recursoTipo = "-", recursoId = "-") {
    const novoLog = {
      id: crypto.randomUUID(),
      usuario: usuario?.email || "sistema",
      evento,
      detalhe,
      recursoTipo,
      recursoId,
      createdAt: new Date().toLocaleString("pt-BR"),
    };

    const novosLogs = [novoLog, ...logs];

    setLogs(novosLogs);
    localStorage.setItem("logs", JSON.stringify(novosLogs));
  }

  function criarSolicitacao(dados) {
    const novaSolicitacao = {
      id: crypto.randomUUID(),
      alunoEmail: usuario.email,
      alunoNome: usuario.nome,
      tipo: dados.tipo,
      disciplina: dados.disciplina,
      descricao: dados.descricao,
      dataOcorrencia: dados.dataOcorrencia,
      status: "PENDENTE",
      createdAt: new Date().toLocaleString("pt-BR"),
    };

    const novasSolicitacoes = [novaSolicitacao, ...solicitacoes];

    setSolicitacoes(novasSolicitacoes);
    localStorage.setItem("solicitacoes", JSON.stringify(novasSolicitacoes));

    registrarLog(
      "CRIACAO_SOLICITACAO",
      "Aluno criou uma nova solicitação.",
      "SOLICITACAO",
      novaSolicitacao.id,
    );
  }

  function alterarStatus(id, novoStatus) {
    const novasSolicitacoes = solicitacoes.map((solicitacao) =>
      solicitacao.id === id
        ? { ...solicitacao, status: novoStatus }
        : solicitacao,
    );

    setSolicitacoes(novasSolicitacoes);
    localStorage.setItem("solicitacoes", JSON.stringify(novasSolicitacoes));

    registrarLog(
      novoStatus === "APROVADA"
        ? "APROVACAO_SOLICITACAO"
        : "REPROVACAO_SOLICITACAO",
      `Solicitação ${novoStatus.toLowerCase()}.`,
      "SOLICITACAO",
      id,
    );
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
        element={protegerPagina(
          <Solicitacoes
            usuario={usuario}
            solicitacoes={solicitacoes}
            alterarStatus={alterarStatus}
          />,
        )}
      />

      <Route
        path="/solicitacoes/nova"
        element={protegerPagina(
          <NovaSolicitacao
            usuario={usuario}
            criarSolicitacao={criarSolicitacao}
          />,
        )}
      />

      <Route
        path="/logs"
        element={protegerPagina(<Logs usuario={usuario} logs={logs} />)}
      />

      <Route
        path="*"
        element={<Navigate to={usuario ? "/dashboard" : "/login"} />}
      />
    </Routes>
  );
}

export default App;
