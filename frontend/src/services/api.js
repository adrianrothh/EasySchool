const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

function getAccessToken() {
  return localStorage.getItem("accessToken");
}

function getRefreshToken() {
  return localStorage.getItem("refreshToken");
}

function authHeaders() {
  const token = getAccessToken();

  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      ...authHeaders(),
      ...options.headers,
    },
  });

  const contentType = response.headers.get("content-type");

  let data = null;

  if (contentType && contentType.includes("application/json")) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    if (path === "/api/auth/login") {
      throw new Error("Email ou senha inválidos.");
    }

    if (response.status === 401) {
      throw new Error("Não autenticado. Faça login novamente.");
    }

    if (response.status === 403) {
      throw new Error("Acesso negado. Você não tem permissão para esta ação.");
    }

    if (typeof data === "string" && data.trim() !== "") {
      throw new Error(data);
    }

    if (data?.message) {
      throw new Error(data.message);
    }

    throw new Error("Erro na requisição.");
  }

  return data || null;
}

export async function login(email, password) {
  return request("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export async function refreshToken() {
  return request("/api/auth/refresh", {
    method: "POST",
    body: JSON.stringify({
      refreshToken: getRefreshToken(),
    }),
  });
}

export async function criarSolicitacao(dados) {
  return request("/api/solicitacoes", {
    method: "POST",
    body: JSON.stringify(dados),
  });
}

export async function buscarMinhasSolicitacoes() {
  return request("/api/solicitacoes/minhas");
}

export async function buscarTodasSolicitacoes() {
  return request("/api/solicitacoes");
}

export async function alterarStatusSolicitacao(id, dados) {
  return request(`/api/solicitacoes/${id}/status`, {
    method: "PUT",
    body: JSON.stringify(dados),
  });
}

export async function buscarLogs() {
  return request("/audit-logs");
}