# Backend — EasySchool

As instruções completas de execução estão no [README principal](./README.md#-como-rodar-o-projeto) na raiz do projeto.

**Resumo rápido:**

```bash
# 1. Copiar o template de configuracao (so na primeira vez)
cp backend/src/main/resources/application.properties.example backend/src/main/resources/application.properties
# (Windows PowerShell)
# Copy-Item backend\src\main\resources\application.properties.example backend\src\main\resources\application.properties

# 2. Subir o banco - escolha A ou B:
docker compose up -d                  # A) Docker
# B) Postgres local: crie o banco "easyschool" e ajuste a senha em application.properties

# 3. Rodar o backend
cd backend
./mvnw spring-boot:run                # Linux/macOS
.\mvnw.cmd spring-boot:run            # Windows
```

API disponível em <http://localhost:8080>.
Healthcheck: <http://localhost:8080/api/health>.

## Configuracao

Toda a configuracao (URL do banco, credenciais, JWT, porta) fica em `backend/src/main/resources/application.properties`. Esse arquivo e **gitignored** — cada desenvolvedor mantem o seu, com a senha do seu Postgres. O template versionado e `application.properties.example`.
