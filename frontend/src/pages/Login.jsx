function Login({ login }) {
  return (
    <div>
      <h1>EasySchool</h1>
      <p>Sistema de solicitações acadêmicas</p>

      <h3>Login temporário</h3>

      <button onClick={() => login("ALUNO")}>Entrar como Aluno</button>
      <br />
      <br />

      <button onClick={() => login("PROFESSOR")}>Entrar como Professor</button>
      <br />
      <br />

      <button onClick={() => login("ADMIN")}>Entrar como Administrador</button>
    </div>
  );
}

export default Login;
