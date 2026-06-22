# Como rodar o backend

## 1. Criar o banco de dados

-Abra o SQL Shell do PostgreSQL e crie o banco:
CREATE DATABASE easyschool;

## 2. Configurar o banco no projeto

-No arquivo "backend/src/main/resources/application.properties" confira se está apontando para o banco local:

spring.datasource.url=jdbc:postgresql://localhost:5432/easyschool
spring.datasource.username=postgres
spring.datasource.password=**sua_senha_do_postgres** `<----  (A senha deve ser a mesma configurada na instalação do PostgreSQL)`

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080

## 3. Rodar o backend

-No terminal, entre na pasta do backend:
cd backend

-Depois execute:
.\mvnw.cmd spring-boot:run

Se tudo estiver certo, o terminal deve mostrar que o Tomcat iniciou na porta 8080:

Tomcat started on port 8080
Started DemoApplication

## 4. Testar se a API está respondendo

-Com o backend rodando, acesse no navegador:

http://localhost:8080/api/health

Resposta esperada:

-EasySchool API rodando
