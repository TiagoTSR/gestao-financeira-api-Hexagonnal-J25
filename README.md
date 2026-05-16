# 💰 Gestão Financeira API — Hexagonal

> API RESTful para gerenciamento de **pessoas, receitas e despesas**, construída com **Arquitetura Hexagonal (Ports & Adapters)**, **Java 25** e **Spring Boot 4**.

---

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Por que Arquitetura Hexagonal?](#por-que-arquitetura-hexagonal)
- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Endpoints](#endpoints)
- [Testes e Cobertura](#testes-e-cobertura)
- [Padrão de Commits](#padrão-de-commits)

---

## Sobre o Projeto

Esta API é uma evolução da versão original construída em **arquitetura em camadas (layered)**, reescrita utilizando **Arquitetura Hexagonal** para explorar os benefícios de isolamento de domínio, testabilidade e baixo acoplamento com frameworks externos.

O sistema permite o gerenciamento de:

- **Pessoas** — cadastro e manutenção de usuários/clientes
- **Lançamentos** — registro de movimentações financeiras (receitas e despesas)
- **Categorias** — classificação dos lançamentos financeiros

---

## Por que Arquitetura Hexagonal?

A **Arquitetura Hexagonal (Ports & Adapters)**, proposta por Alistair Cockburn, isola o núcleo de negócio (domínio) de detalhes de infraestrutura como banco de dados, frameworks e APIs externas.

| Vantagem | Descrição |
|---|---|
| 🧪 **Testes mais rápidos** | O domínio pode ser testado sem Spring, sem banco, sem HTTP |
| 🔌 **Baixo acoplamento** | Fácil trocar o banco de dados, o framework ou adicionar novos adaptadores |
| 🧩 **Separação clara** | Lógica de negócio não conhece detalhes de infraestrutura |
| 📦 **Portabilidade** | Integrar uma nova API externa não quebra o domínio existente |

```
┌──────────────────────────────────────────────┐
│                  ADAPTADORES                 │
│  ┌──────────┐              ┌──────────────┐  │
│  │  REST    │              │  PostgreSQL  │  │
│  │ (entrada)│              │   (saída)    │  │
│  └────┬─────┘              └──────┬───────┘  │
│       │        DOMÍNIO            │          │
│  ┌────▼───────────────────────────▼──────┐   │
│  │         Ports & Use Cases             │   │
│  │   (Pessoas | Receitas | Despesas)     │   │
│  └───────────────────────────────────────┘   │
└──────────────────────────────────────────────┘
```

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 25 | Linguagem principal |
| Spring Boot | 4.0.6 | Framework web |
| Spring Data JPA | — | Persistência |
| Spring Validation | — | Validação de entrada |
| PostgreSQL | — | Banco de dados |
| Flyway | — | Migrations de banco |
| JaCoCo | 0.8.14 | Cobertura de testes |
| Maven | — | Gerenciamento de dependências |

---

## Estrutura do Projeto

```
src/
└── main/
    └── java/com/decodex/br/
        ├── domain/                  # Núcleo de negócio (sem dependências externas)
        │   ├── model/               # Entidades e objetos de valor
        │   └── port/                # Interfaces (Ports)
        │       ├── in/              # Ports de entrada (use cases)
        │       └── out/             # Ports de saída (repositórios)
        ├── application/             # Implementação dos use cases
        │   └── usecase/
        └── adapter/                 # Adaptadores (infraestrutura)
            ├── in/
            │   └── web/             # Controllers REST
            └── out/
                └── persistence/     # Repositórios JPA
```

---

## Pré-requisitos

- Java 25+
- Maven 3.8+
- PostgreSQL rodando localmente (ou via Docker)

---

## Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/TiagoTSR/gestao-financeira-api-Hexagonnal-J25.git
cd gestao-financeira-api-Hexagonnal-J25
```

### 2. Configure o banco de dados

Crie um banco PostgreSQL e configure as credenciais em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestao_financeira
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 3. Execute a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

> As migrations do Flyway serão executadas automaticamente ao iniciar a aplicação.

---

## Endpoints

### Pessoas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/pessoas` | Lista todas as pessoas |
| `GET` | `/pessoas/{id}` | Busca pessoa por ID |
| `POST` | `/pessoas` | Cadastra nova pessoa |
| `PUT` | `/pessoas/{id}` | Atualiza pessoa |
| `DELETE` | `/pessoas/{id}` | Remove pessoa |

### Lançamentos

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/lancamentos` | Lista todos os lançamentos |
| `GET` | `/lancamentos/{id}` | Busca lançamento por ID |
| `POST` | `/lancamentos` | Cadastra novo lançamento |
| `PUT` | `/lancamentos/{id}` | Atualiza lançamento |
| `DELETE` | `/lancamentos/{id}` | Remove lançamento |

### Categorias

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/categorias` | Lista todas as categorias |
| `GET` | `/categorias/{id}` | Busca categoria por ID |
| `POST` | `/categorias` | Cadastra nova categoria |
| `PUT` | `/categorias/{id}` | Atualiza categoria |
| `DELETE` | `/categorias/{id}` | Remove categoria |

---

## Testes e Cobertura

O projeto utiliza **JaCoCo** para monitoramento de cobertura de testes, com uma meta mínima de **80% de cobertura de linhas**.

```bash
# Executar testes
./mvnw test

# Gerar relatório de cobertura
./mvnw verify

# O relatório HTML é gerado em:
# target/site/jacoco/index.html
```

> **Nota:** As classes de `dto`, `config` e `exception` estão excluídas da contagem de cobertura por convenção do projeto.

---

## Padrão de Commits

Este projeto segue um padrão simplificado de **Conventional Commits**:

| Prefixo | Uso |
|---|---|
| `feat:` | Nova funcionalidade |
| `fix:` | Correção de bug |
| `refactor:` | Refatoração sem mudança de comportamento |
| `test:` | Adição ou correção de testes |
| `docs:` | Alterações na documentação |
| `chore:` | Tarefas de manutenção (deps, config) |

**Exemplo:**
```
feat: adiciona endpoint de criação de despesa
fix: corrige validação de valor negativo em receita
test: adiciona testes unitários para DespesaUseCase
```

---

## Comparação com a Versão Layered

| Aspecto | Versão Layered | Versão Hexagonal (este projeto) |
|---|---|---|
| Arquitetura | Controller → Service → Repository | Adapter → Port → Use Case → Port → Adapter |
| Acoplamento | Alto (Spring em todo lugar) | Baixo (domínio puro) |
| Testabilidade | Requer contexto Spring | Testes unitários rápidos sem framework |
| Manutenção | Mudanças tendem a cascatear | Impacto isolado por camada |

---

## Autor

**TiagoTSR** — [github.com/TiagoTSR](https://github.com/TiagoTSR)
