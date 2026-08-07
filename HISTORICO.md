# Histórico de Atividades do Projeto

Este arquivo registra todas as tarefas e implementações realizadas no projeto de Gestão Financeira.

---

## [2026-08-07] - Implementação do Swagger/OpenAPI

**Objetivo:**
Integrar o Swagger/OpenAPI no projeto de forma limpa, utilizando arquitetura hexagonal, separando a documentação dos controladores de fluxo através de interfaces dedicadas.

### Atividades Realizadas e Commits:

1. **Configuração de Dependências:**
   - Adicionada a dependência `springdoc-openapi-starter-webmvc-ui` (versão `2.8.5`) ao arquivo `pom.xml`.
   - **Commit:** `chore(deps): adicionar dependência do springdoc-openapi no pom.xml`

2. **Classe de Configuração OpenAPI:**
   - Criada a classe `com.decodex.br.config.OpenApiConfig` para registrar as informações básicas da API (título, descrição, versão e contato).
   - **Commit:** `feat(config): adicionar classe OpenApiConfig para Swagger`

3. **Criação do Pacote e Interfaces de Documentação:**
   - Criado o pacote `com.decodex.br.adapters.in.web.documentation`.
   - Criadas as seguintes interfaces de documentação com as anotações do Swagger/OpenAPI (`@Tag`, `@Operation`, `@ApiResponses`, `@Parameter`):
     - `CategoriaControllerDoc.java`
     - `LancamentoControllerDoc.java`
     - `PessoaControllerDoc.java`
     - `RelatorioControllerDoc.java`
   - **Commit:** `feat(web): criar interfaces de documentação no pacote documentation`

4. **Refatoração dos Controladores:**
   - Atualizados os controladores para implementar suas respectivas interfaces de documentação, herdando todas as definições do Swagger sem poluir a classe de controle:
     - `CategoriaController.java`
     - `LancamentoController.java`
     - `PessoaController.java`
     - `RelatorioController.java`
   - **Commit:** `refactor(web): implementar interfaces de documentação nos controllers`

5. **Acompanhamento de Tarefas:**
   - Criação dos arquivos `implementation_plan.md`, `task.md` e `HISTORICO.md` para planejamento, monitoramento e registro das tarefas.
   - **Commit:** `docs: criar plano de implementação, tarefas e histórico de atividades`

---

## [2026-08-07] - Resolução de Conflito de Portas do Banco de Dados (PostgreSQL)

**Objetivo:**
Corrigir o erro de autenticação e falha na inicialização do contexto do Spring Boot causado pelo conflito de portas da porta padrão `5432` do PostgreSQL.

### Atividades Realizadas e Commits:

1. **Identificação do Conflito:**
   - Detectado que o Docker Compose falhava ao subir o container do PostgreSQL na porta `5432` pois ela já estava em uso por outro serviço local (PostgreSQL nativo do Windows).
   - Identificado que o Spring Boot conectava ao banco nativo usando as credenciais do `.env`, resultando em falha de autenticação (`FATAL: password authentication failed`).

2. **Alteração de Porta:**
   - O mapeamento do banco de dados no `docker-compose.yml` foi ajustado para a porta externa `5435` devido a conflitos detectados nas portas `5432` e `5433`.
   - **Commit:** `fix(docker): alterar porta do postgres no docker-compose e atualizar env`

3. **Ajuste de Configuração Local:**
   - Modificado o arquivo `.env` para atualizar a variável `DB_URL2` para `jdbc:postgresql://localhost:5435/rest_spring`, alinhando a aplicação com a nova porta exposta pelo container.

4. **Remoção de Volumes Antigos (Persistência de Senha Incorreta):**
   - Identificado que o PostgreSQL em container mantém a senha antiga persistida em volumes anônimos criados pelo Docker. Realizada a execução do comando `docker compose down -v` para limpar estes volumes anônimos e forçar a inicialização do banco com a nova senha definida no `.env`.

5. **Fixação Estática de Credenciais no Docker Compose:**
   - Para evitar conflitos de interpolação ou sobreposição com variáveis de ambiente globais do sistema operacional do host Windows (que possuem precedência maior que o arquivo `.env`), os valores de `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` foram inseridos de forma literal (hardcoded) diretamente no arquivo `docker-compose.yml`.
   - **Commit:** `fix(docker): hardcode de credenciais no docker-compose para evitar conflitos de env`
