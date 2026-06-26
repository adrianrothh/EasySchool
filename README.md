# EasySchool

Sistema web para gerenciamento de solicitações acadêmicas de revisão de notas e abono de faltas, desenvolvido para a disciplina de Segurança da Informação.

> **Para o professor avaliador:** o guia rápido de execução está logo abaixo, em [Como rodar o projeto](#-como-rodar-o-projeto). Se já tiver Docker instalado, são 5 comandos do clone até o sistema no ar.

---

## 🚀 Como rodar o projeto

### Pré-requisitos

| Obrigatório | Versão recomendada |
|---|---|
| **Java JDK** | 17 ou superior |
| **Node.js** | 20 ou superior (inclui `npm`) |
| **Git** | qualquer |

Para o banco de dados, escolha **UMA** das opções:
- **Opção A (mais simples):** [Docker Desktop](https://www.docker.com/products/docker-desktop/) — já traz tudo pronto
- **Opção B:** [PostgreSQL 14+](https://www.postgresql.org/download/) instalado localmente

> Não precisa instalar Maven — o projeto usa o wrapper `mvnw`.

---

### 1. Clonar o repositório

```bash
git clone https://github.com/<usuario>/EasySchool.git
cd EasySchool
```

### 2. Criar o `application.properties`

Na pasta `backend/src/main/resources/`, copie o arquivo de exemplo:

**Windows (PowerShell):**
```powershell
Copy-Item backend\src\main\resources\application.properties.example backend\src\main\resources\application.properties
```

**Linux/macOS:**
```bash
cp backend/src/main/resources/application.properties.example backend/src/main/resources/application.properties
```

> Esse arquivo é **ignorado pelo git** — cada pessoa tem o seu, com sua própria senha do banco. Para o caso Docker, os valores padrão já funcionam.

---

### 3. Subir o banco de dados

#### 🅰️ Opção A — PostgreSQL via Docker (recomendado)

Na raiz do projeto:

```bash
docker compose up -d
```

Pronto. O banco sobe na porta **`15432`** do host (mapeada para a 5432 do container) com usuário `postgres`, senha `postgres` e banco `easyschool` — mesmos valores do `application.properties.example`. Usamos `15432` em vez de `5432` para não colidir com instalações locais de PostgreSQL.

Para parar:

```bash
docker compose down
```

Para também subir o **pgAdmin** (interface web em <http://localhost:5050>, login `admin@easyschool.local` / `admin`):

```bash
docker compose --profile tools up -d
```

> Dentro do pgAdmin, ao registrar o servidor, use **`host: postgres`** e **`port: 5432`** (o pgAdmin fala com o Postgres pela rede interna do Docker). De ferramentas fora do Docker (DBeaver, psql na sua máquina), use `localhost` na porta `15432`.

#### 🅱️ Opção B — PostgreSQL local

1. Abra o **pgAdmin** ou o **psql** e crie o banco:

   ```sql
   CREATE DATABASE easyschool;
   ```

2. Edite `backend/src/main/resources/application.properties` e ajuste para refletir a sua instalação local:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/easyschool
   spring.datasource.username=postgres
   spring.datasource.password=<a_senha_do_seu_postgres>
   ```

   > Se o seu Postgres estiver em outra porta, troque também na URL.

> As tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`) na primeira execução do backend.

---

### 4. Rodar o backend

```bash
cd backend
```

**Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**
```bash
./mvnw spring-boot:run
```

> Você também pode rodar direto pelo IntelliJ: abra a classe `DemoApplication.java` e clique em **Run**.

Se tudo deu certo, aparecerá algo como:

```
Tomcat started on port 8080
Started DemoApplication
```

Teste no navegador: <http://localhost:8080/api/health> → deve responder **EasySchool API rodando**.

#### Usuários de teste

Na primeira execução, o backend cria três usuários (senha: **`123456`** em todos):

| Perfil | E-mail | Senha |
|---|---|---|
| Aluno | `aluno@easyschool.com` | `123456` |
| Professor | `professor@easyschool.com` | `123456` |
| Administrador | `admin@easyschool.com` | `123456` |

---

### 5. Rodar o frontend

Em outro terminal, na raiz do projeto:

```bash
cd frontend
npm install      # só na primeira vez
npm run dev
```

Abra o endereço que aparecer no terminal (normalmente <http://localhost:5173>).

---

## 🧯 Solução de problemas comuns

| Problema | Causa provável | Solução |
|---|---|---|
| `password authentication failed for user "postgres"` mesmo com a senha certa | Há um PostgreSQL local rodando na porta indicada no `application.properties` e o Spring está conectando nele em vez do container Docker | Confira em quem responde: `Get-NetTCPConnection -LocalPort 15432 -State Listen` (PowerShell). Se não for o `com.docker.backend`, pare o serviço local (`Stop-Service postgresql-x64-XX` como Admin) ou troque a porta exposta no `docker-compose.yml` e na URL do `application.properties` |
| `Connection refused` no backend | Banco não está rodando | Rode `docker compose up -d` ou inicie o serviço do Postgres local |
| `Port 15432 already in use` ao rodar `docker compose up` | Já existe algo escutando na 15432 | Troque a porta exposta no `docker-compose.yml` (ex.: `"25432:5432"`) e ajuste a URL em `application.properties` |
| Senha continua errada após mudar o `POSTGRES_PASSWORD` no `docker-compose.yml` | A senha do Postgres é gravada no volume na **primeira** inicialização; em runs seguintes a variável é ignorada | Recrie o volume: `docker compose down -v && docker compose up -d` (apaga os dados do banco) |
| `mvnw` não executa no Linux/macOS | Sem permissão | `chmod +x backend/mvnw` |
| Porta 8080 ocupada | Outro serviço usando 8080 | Edite `server.port=8081` no `application.properties` |
| Avisos "Unused property" no IntelliJ | Falso positivo do plugin do Spring | Pode ignorar — são lidas em runtime |

---

## 📚 Descrição do projeto

O EasySchool tem como objetivo facilitar e organizar o processo de solicitações acadêmicas dentro de uma instituição de ensino. A plataforma permite que alunos realizem pedidos de revisão de notas ou abono de faltas, enquanto os responsáveis pela análise podem avaliar, aprovar ou reprovar as solicitações.

O sistema mantém histórico completo das ações realizadas e registros de auditoria, garantindo rastreabilidade, transparência e segurança das informações.

### Objetivos

- Centralizar solicitações acadêmicas em uma única plataforma.
- Garantir controle de acesso baseado em perfis de usuário.
- Registrar ações importantes através de logs de auditoria.
- Aplicar conceitos de segurança da informação estudados na disciplina.
- Demonstrar autenticação, autorização e rastreabilidade em um sistema real.

### Integrantes

- Adrian Marcio Roth
- Gustavo Franz
- Leonardo André Ferreira
- Marcelo Gustavo Eger
- Willian Squena

### Tecnologias

- **Frontend:** React + Vite + TailwindCSS
- **Backend:** Java 17 + Spring Boot 4
- **Banco de Dados:** PostgreSQL 16
- **Segurança:** Spring Security, JWT, BCrypt
- **Build:** Maven Wrapper, npm

### Perfis de usuário

**Aluno** — criar e acompanhar solicitações de revisão de nota e abono de falta.
**Professor** — visualizar, analisar e dar parecer nas solicitações das suas disciplinas.
**Administrador** — gerenciar usuários e permissões, aprovar/reprovar solicitações, consultar logs.

### Requisitos de segurança aplicados

- Autenticação via JWT
- Autorização baseada em perfis (Spring Security)
- Senhas armazenadas com hash **BCrypt**
- Validação de entrada no backend
- Logs de auditoria (login, criação/alteração/aprovação/reprovação, acesso negado)
- Configuração local fora do controle de versão (`application.properties` é gitignored)

---

## 📂 Estrutura

```
EasySchool/
├── backend/                                 # API Spring Boot (Java 17)
│   └── src/main/resources/
│       ├── application.properties           # gitignored - sua config local
│       └── application.properties.example   # template versionado
├── frontend/                                # Aplicação React + Vite
├── docs/                                    # Diagramas, evidências, modelagem
├── docker-compose.yml                       # PostgreSQL + pgAdmin
└── README.md                                # Este arquivo
```

---

## 📄 Licença

Projeto desenvolvido exclusivamente para fins educacionais.
