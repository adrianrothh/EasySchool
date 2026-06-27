import { useState } from "react";
import { login } from "../services/api";

function Login({ salvarSessao }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function entrar(event) {
    event.preventDefault();

    setErro("");
    setCarregando(true);

    try {
      const tokens = await login(email, password);
      salvarSessao(email, tokens);
    } catch (error) {
      setErro(error.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-sm bg-white rounded-xl shadow-md border border-slate-200 p-6">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-slate-900">EasySchool</h1>
          <p className="text-sm text-slate-500 mt-1">Solicitações acadêmicas</p>
        </div>

        <form onSubmit={entrar} className="space-y-4" autoComplete="off">
          <div>
            <label className="block text-sm text-left font-medium text-slate-700 mb-1">
              Email
            </label>

            <input
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
              type="email"
              name="easyschool-email"
              autoComplete="off"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </div>

          <div>
            <label className="block text-left text-sm font-medium text-slate-700 mb-1">
              Senha
            </label>

            <input
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
              type="password"
              name="easyschool-password"
              autoComplete="new-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            disabled={carregando}
            className="w-full rounded-lg bg-blue-600 text-white py-2 text-sm font-semibold hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed transition"
          >
            {carregando ? "Entrando..." : "Entrar"}
          </button>
        </form>

        {erro && (
          <div className="mt-4 rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
            {erro}
          </div>
        )}
      </div>
    </div>
  );
}

export default Login;
