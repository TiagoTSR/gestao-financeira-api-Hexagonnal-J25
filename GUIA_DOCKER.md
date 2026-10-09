# 🐳 Guia de Execução com Docker — Gestão Financeira API

Este guia prático explica o passo a passo para subir o ambiente no Docker antes de iniciar a aplicação. Você pode consultar este arquivo sempre que precisar relembrar os comandos.

---

## ⚠️ Passo 0: Pré-requisito Obrigatório (Docker Desktop)

Antes de rodar qualquer comando no terminal ou dar Play na IDE:
1. Abra o **Docker Desktop** no Windows pelo menu Iniciar.
2. Aguarde até que o ícone da baleia na barra de tarefas fique verde / estável (**"Engine running"**).

> **Por que a aplicação falhou antes?**
> - Quando o Docker Desktop está fechado, o serviço não responde (`error during connect ... connectex`).
> - Ao tentar iniciar a API no Spring sem o container do banco ativo, ocorre o erro `The postmaster is accepting TCP/IP connections` porque o banco na porta `5435` não foi encontrado.

---

## 🎯 Qual cenário você quer usar?

### 🔹 Cenário 1: Rodar o Banco no Docker e a API na IDE (Recomendado para Desenvolvimento)

Neste fluxo, o **PostgreSQL roda dentro do Docker** e você executa / depura o código da API diretamente pelo **VS Code, Eclipse, IntelliJ** ou terminal.

#### 1. Inicie o PostgreSQL no Docker:
No terminal da raiz do projeto, execute:
```bash
docker compose up -d meu-postgres
```
*(Ou dê um duplo clique no arquivo [`docker-start-db.bat`](file:///c:/Programas%20Baixados/gestao-financeira-api-Hexagonnal-J25/docker-start-db.bat))*

#### 2. Inicie a aplicação Spring Boot:
- **Pela IDE:** Abra [`GestaoFinanceiraApiHexagonalJ25Application.java`](file:///c:/Programas%20Baixados/gestao-financeira-api-Hexagonnal-J25/src/main/java/com/decodex/br/GestaoFinanceiraApiHexagonalJ25Application.java) e clique em **Run / Debug**.
- **Pelo Terminal:**
  ```bash
  .\mvnw.cmd spring-boot:run
  ```

* **API:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **PostgreSQL:** porta `5435` (usuário: `postgres`, banco: `rest_spring`, senha: `T2143Lo67r8`)

---

### 🔹 Cenário 2: Rodar TUDO no Docker (Banco + API)

Use este fluxo quando quiser validar o pacote de produção completo sem precisar de Java ou Maven instalados na máquina.

#### 1. Execute o build e suba todos os contêineres:
```bash
docker compose up -d --build
```
*(Ou dê um duplo clique no arquivo [`docker-start-app.bat`](file:///c:/Programas%20Baixados/gestao-financeira-api-Hexagonnal-J25/docker-start-app.bat))*

* **API:** `http://localhost:8081` *(porta mapeada pelo docker-compose para não conflitar com a 8080 local)*
* **Swagger UI:** `http://localhost:8081/swagger-ui.html`
* **PostgreSQL:** porta `5435`

---

## 📋 Cola Rápida de Comandos (Cheatsheet)

| Ação desejada | Comando Terminal | Atalho Windows |
| :--- | :--- | :--- |
| **Subir apenas o Banco** | `docker compose up -d meu-postgres` | Duplo clique em `docker-start-db.bat` |
| **Subir Banco + API** | `docker compose up -d --build` | Duplo clique em `docker-start-app.bat` |
| **Ver contêineres ativos** | `docker compose ps` | — |
| **Ver logs do Banco** | `docker compose logs -f meu-postgres` | — |
| **Ver logs da API** | `docker compose logs -f minha-api-spring` | — |
| **Parar os contêineres** | `docker compose down` | Duplo clique em `docker-stop.bat` |
| **Resetar banco do zero (apagar dados)** | `docker compose down -v` | — |

---

## 🛠️ Resolução de Problemas Comuns

### 1. `error during connect: dial tcp [::1]:2375: connectex...`
* **Causa:** O Docker Desktop está desligado ou inicializando.
* **Solução:** Abra o Docker Desktop e espere ele inicializar completamente antes de rodar os comandos.

### 2. `Connection to localhost:5435 refused` ou `postmaster is accepting TCP/IP connections`
* **Causa:** O container do PostgreSQL não foi iniciado antes da API.
* **Solução:** Execute `docker compose up -d meu-postgres` e verifique com `docker compose ps` se ele está com status `Up`.

### 3. `Bind for 0.0.0.0:5435 failed: port is already allocated`
* **Causa:** A porta `5435` já está em uso por outro container ou serviço.
* **Solução:** Pare outros containers com `docker stop $(docker ps -q)` ou altere a porta mapeada no [`docker-compose.yml`](file:///c:/Programas%20Baixados/gestao-financeira-api-Hexagonnal-J25/docker-compose.yml).

### 4. Preciso resetar o banco e aplicar todas as migrations do Flyway do zero:
```bash
docker compose down -v
docker compose up -d meu-postgres
```
O parâmetro `-v` remove o volume persistente `gestao_financeira_postgres_data`. Ao reiniciar, o Flyway reexecutará automaticamente todas as migrations (`V01` até `V07`).
