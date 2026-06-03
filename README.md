# EasySchool

Sistema web para gerenciamento de solicitações acadêmicas de revisão de notas e abono de faltas, desenvolvido para a disciplina de Segurança da Informação.

## Descrição

O EasySchool tem como objetivo facilitar e organizar o processo de solicitações acadêmicas dentro de uma instituição de ensino. A plataforma permite que alunos realizem pedidos de revisão de notas ou abono de faltas, enquanto os responsáveis pela análise podem avaliar, aprovar ou reprovar as solicitações.

O sistema mantém histórico completo das ações realizadas e registros de auditoria, garantindo rastreabilidade, transparência e segurança das informações.

## Objetivos do Projeto

- Centralizar solicitações acadêmicas em uma única plataforma.
- Garantir controle de acesso baseado em perfis de usuário.
- Registrar ações importantes através de logs de auditoria.
- Aplicar conceitos de segurança da informação estudados na disciplina.
- Demonstrar autenticação, autorização e rastreabilidade em um sistema real.

---

## Integrantes

- Adrian Marcio Roth
- Gustavo Franz
- Leonardo André Ferreira
- Marcelo Gustavo Eger
- Willian Squena

---

## Tecnologias Utilizadas

### Frontend
- React

### Backend
- Java
- Spring Boot

### Banco de Dados
- PostgreSQL

### Ferramentas Complementares
- Git
- GitHub
- Maven
- JWT (planejado)
- BCrypt (planejado)

---

## Perfis de Usuário

### Aluno
- Criar solicitações de revisão de nota.
- Criar solicitações de abono de falta.
- Consultar suas próprias solicitações.
- Acompanhar o andamento das análises.

### Professor
- Visualizar solicitações relacionadas às suas disciplinas.
- Analisar solicitações.
- Adicionar pareceres.
- Encaminhar solicitações para decisão.

### Administrador
- Gerenciar usuários.
- Gerenciar permissões.
- Aprovar ou reprovar solicitações.
- Visualizar relatórios.
- Consultar logs de auditoria.

---

## Funcionalidades Previstas

### Autenticação
- Login
- Logout
- Controle de sessão

### Usuários
- Cadastro de usuários
- Gerenciamento de perfis
- Controle de permissões

### Solicitações Acadêmicas
- Criar solicitação
- Editar solicitação (quando permitido)
- Consultar solicitações
- Alterar status
- Aprovar solicitação
- Reprovar solicitação

### Auditoria
- Registro de login
- Registro de criação de solicitações
- Registro de alterações
- Registro de aprovações e reprovações
- Registro de tentativas de acesso negado

### Administração
- Painel administrativo
- Gerenciamento de usuários
- Consulta de logs

---

## Requisitos de Segurança

- Autenticação de usuários.
- Autorização baseada em perfis.
- Senhas armazenadas utilizando hash BCrypt.
- Validação de dados no backend.
- Registro de logs de auditoria.
- Proteção de variáveis sensíveis através de arquivo `.env`.
- Controle de acesso a recursos por proprietário e perfil.
- Registro de tentativas de acesso não autorizado.

---

## Status do Projeto

Em desenvolvimento

Projeto acadêmico desenvolvido para a disciplina de Segurança da Informação.

---

## Licença

Projeto desenvolvido exclusivamente para fins educacionais.
