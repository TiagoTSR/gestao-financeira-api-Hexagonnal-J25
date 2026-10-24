# 💰 Gestão Financeira API (Hexagonal Architecture)

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3-FF6600.svg)](https://www.rabbitmq.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

API RESTful corporativa para gestão financeira pessoal e empresarial, reescrita em **Arquitetura Hexagonal (Ports & Adapters)** com **Java 25**, **Spring Boot 4**, mensageria com **RabbitMQ**, segurança **JWT Stateless**, auditoria com **Hibernate Envers** e banco de dados **PostgreSQL**.

---

## 🌐 Ecossistema do Projeto

Este backend foi desenvolvido para ser consumido pela interface web Single Page Application (SPA):

- **Frontend Angular:** [gestao-financeira-frontAngular](https://github.com/TiagoTSR/gestao-financeira-frontAngular)
  - Construído com **Angular 21**, **PrimeNG 21**, Signals reativos, interceptor de autenticação JWT, Dashboard interativo, gestão de lançamentos e exportação de relatórios em tempo real.
- **Deploy em Produção:**
  - **API (Railway):** `https://gestao-financeira-api-hexagonnal-j25-production.up.railway.app`
  - **Interface (Vercel):** `https://gestao-financeira-front-angular.vercel.app`

---

## 📌 Sumário
- [Por que Arquitetura Hexagonal?](#-por-que-arquitetura-hexagonal)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Módulos e Recursos](#-módulos-e-recursos)
  - [Autenticação & Segurança (JWT Stateless)](#1-autenticação--segurança-jwt-stateless)
  - [Gestão Financeira](#2-gestão-financeira)
  - [Relatórios Executivos (PDF & Excel)](#3-relatórios-executivos-pdf--excel)
  - [Processamento Assíncrono com RabbitMQ](#4-processamento-assíncrono-com-rabbitmq)
  - [Auditoria com Hibernate Envers](#5-auditoria-e-rastreabilidade-hibernate-envers)
- [Endpoints da API](#-endpoints-da-api)
- [Variáveis de Ambiente (.env)](#-variáveis-de-ambiente-env)
- [Como Executar Localmente](#-como-executar-localmente)
- [Documentação OpenAPI / Swagger](#-documentação-openapi--swagger)
- [Testes e Cobertura (JaCoCo)](#-testes-e-cobertura-jacoco)
- [Padrão de Commits](#-padrão-de-commits)
- [Autor](#-autor)

---

## 🏛️ Por que Arquitetura Hexagonal?

A **Arquitetura Hexagonal (Ports & Adapters)**, proposta por Alistair Cockburn, desacopla completamente as regras de negócio centrais (Domínio) dos detalhes técnicos de infraestrutura (bancos de dados, frameworks web, filas e mensageria).

```
                      +-----------------------------------+
                      |            ADAPTADORES            |
                      |  +-----------------------------+  |
                      |  |     REST Controller (In)    |  |
                      |  +--------------+--------------+  |
                      |                 |                 |
                      |       (Input Ports / Use Cases)   |
                      |                 v                 |
                      |  +-----------------------------+  |
                      |  |           DOMÍNIO           |  |
                      |  |  Entidades | Regras | Model |  |
                      |  +--------------+--------------+  |
                      |                 |                 |
                      |      (Output Ports / Spies)       |
                      |                 v                 |
                      |  +-----------------------------+  |
                      |  |  JPA / RabbitMQ / PDF (Out) |  |
                      |  +-----------------------------+  |
                      +-----------------------------------+
```

| Vantagem | Descrição |
|---|---|
| 🧪 **Testabilidade Pura** | O domínio pode ser 100% testado sem carregar Spring, Docker ou banco de dados |
| 🔌 **Baixo Acoplamento** | É possível trocar o banco ou a fila sem alterar nenhuma regra de negócio |
| 🛡️ **Segurança Arquitetural** | O núcleo da aplicação não possui anotações de frameworks externos |
| 📈 **Manutenibilidade** | Regras de validação residem estritamente no domínio |

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Função no Projeto |
|---|---|---|
| **Java** | 25 | Linguagem principal com recursos modernos |
| **Spring Boot** | 4.0.6 | Framework web e injeção de dependências |
| **Spring Security** | 7.x | Autenticação e autorização Stateless com JWT |
| **PostgreSQL** | 16 | Banco de dados relacional principal |
| **RabbitMQ** | 3.x | Mensageria assíncrona para geração de relatórios |
| **Flyway** | 11.x | Versionamento e migração automática de banco de dados |
| **Hibernate Envers** | 7.x | Auditoria e histórico de modificações de registros |
| **OpenHtmlToPdf** | 1.1.x | Motor de renderização de PDF via HTML5/CSS3 |
| **Apache POI** | 5.4.x | Geração e estilização de planilhas Excel (.xlsx) |
| **Bucket4j** | 8.x | Rate Limiting e prevenção de ataques DoS |
| **Dotenv Java** | 3.2.x | Gerenciamento de variáveis em arquivos `.env` |
| **JaCoCo** | 0.8.14 | Métricas e relatório de cobertura de testes |

---

## 📂 Estrutura do Projeto

```
src/main/java/com/decodex/br/
├── domain/                    # Núcleo de negócio (Zero dependência de frameworks)
│   ├── model/                # Entidades ricas (Lancamento, Pessoa, Categoria, Usuario)
│   ├── port/                 # Portas (Interfaces)
│   │   ├── in/               # Portas de Entrada (Use cases de regras de negócio)
│   │   └── out/              # Portas de Saída (Contratos de persistência e relatórios)
│   ├── service/              # Implementação de regras de serviço do domínio
│   └── validations/          # Validadores puros de negócio
├── application/              # Camada de aplicação
│   ├── dto/                  # Objetos de transferência de dados (Request/Response)
│   └── usecase/              # Casos de uso especializados
├── adapters/                 # Adaptadores de Infraestrutura
│   ├── in/                   # Adaptadores de Entrada
│   │   ├── web/              # Controladores REST Spring MVC
│   │   └── messaging/        # Consumidores RabbitMQ (Queues e DLQ)
│   └── out/                  # Adaptadores de Saída
│       ├── persistence/      # Repositórios Spring Data JPA e Mappers
│       ├── pdf/              # Geração de PDF via OpenHtmlToPdf + Thymeleaf
│       ├── excel/            # Geração de planilhas via Apache POI
│       └── messaging/        # Produtor RabbitMQ
└── config/                   # Configurações Spring (Security, CORS, RabbitMQ, RateLimit)
```

---

## 🚀 Módulos e Recursos

### 1. Autenticação & Segurança (JWT Stateless)
- **Stateless Session (`SessionCreationPolicy.STATELESS`)**: Sem cookies ambientais vulneráveis a CSRF.
- **Autenticação via Header**: `Authorization: Bearer <token>`.
- **Rotação de Tokens**: Suporte a Refresh Token de longa duração.
- **Rate Limiting**: Controle de taxa de requisições por IP.

### 2. Gestão Financeira
- **Lançamentos**: Controle de **Receitas** e **Despesas** com:
  - Suporte a parcelamentos (`1/3`, `2/3`, etc.).
  - Estados financeiros (`PAGO`, `RECEBIDO`, `PENDENTE`, `CANCELADO`).
  - Datas de vencimento e pagamento.
  - Filtros avançados com paginação (`Pageable`).
- **Pessoas**: Cadastro e gerenciamento de favorecidos/devedores com controle de status ativo/inativo.
- **Categorias**: Organização orçamentária dos lançamentos.

### 3. Relatórios Executivos (PDF & Excel)
- **PDF Corporativo (`/relatorios/lancamentos-por-pessoa`)**:
  - Renderizado com OpenHtmlToPdf e Thymeleaf.
  - Layout corporativo A4 com cards de KPIs (Receitas, Despesas, Saldo Líquido, Lançamentos).
  - Tabela zebrada com badges de status e paginação dinâmica (`Página X de Y`).
  - Totalmente seguro contra falhas de encoding com caracteres UTF-8 nativos.
- **Planilhas Excel (`/relatorios/lancamentos-por-pessoa/excel`)**:
  - Geração nativa em formato `.xlsx` usando Apache POI.
  - Formatação monetária (`R$ #,##0.00`) e fórmulas de soma automáticas (`SUM`).
  - Abas segmentadas: *Lançamentos Detalhados* e *Resumo por Pessoa*.

### 4. Processamento Assíncrono com RabbitMQ
Para grandes volumes de dados, os relatórios podem ser solicitados de forma não-bloqueante:
1. O cliente faz um `POST /api/relatorios/solicitar`.
2. A solicitação é persistida como `PENDENTE` e enviada para a fila RabbitMQ `relatorios.solicitacao`.
3. Um consumidor em segundo plano processa os dados, gera o arquivo e atualiza o status para `CONCLUIDO`.
4. O cliente consulta o status via `GET /api/relatorios/{protocolo}/status` e faz o download em `GET /api/relatorios/{protocolo}/download`.
5. Se ocorrer falha repetida, a mensagem é direcionada automaticamente para a **Dead Letter Queue (DLQ)** `relatorios.dlq`.
6. Painel interativo de consulta disponível em `GET /api/relatorios/painel`.

### 5. Auditoria e Rastreabilidade (Hibernate Envers)
- Todas as mutações nas entidades geram registros automáticos de histórico nas tabelas `_AUD`.
- Permite saber exatamente o valor anterior, a data e a revisão de cada alteração financeira.

---

## 📡 Endpoints da API

### Autenticação
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/auth/register` | Cadastra um novo usuário no sistema |
| `POST` | `/auth/login` | Autentica e retorna tokens JWT |
| `POST` | `/auth/refresh` | Renova o Access Token usando Refresh Token |

### Lançamentos
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/lancamentos` | Lista lançamentos paginados com filtros |
| `GET` | `/lancamentos/{id}` | Busca lançamento por UUID |
| `POST` | `/lancamentos` | Cadastra novo lançamento |
| `PUT` | `/lancamentos/{id}` | Atualiza dados do lançamento |
| `DELETE` | `/lancamentos/{id}` | Remove lançamento existente |

### Pessoas
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/pessoas` | Lista pessoas cadastradas |
| `GET` | `/pessoas/{id}` | Busca pessoa por UUID |
| `POST` | `/pessoas` | Cadastra nova pessoa |
| `PUT` | `/pessoas/{id}` | Atualiza pessoa |
| `DELETE` | `/pessoas/{id}` | Remove pessoa |

### Categorias
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/categorias` | Lista todas as categorias |
| `GET` | `/categorias/{id}` | Busca categoria por UUID |
| `POST` | `/categorias` | Cadastra nova categoria |
| `PUT` | `/categorias/{id}` | Atualiza categoria |
| `DELETE` | `/categorias/{id}` | Remove categoria |

### Relatórios
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/relatorios/lancamentos-por-pessoa` | Emite relatório analítico em **PDF** |
| `GET` | `/relatorios/lancamentos-por-pessoa/excel` | Exporta relatório analítico em **Excel (.xlsx)** |
| `POST` | `/api/relatorios/solicitar` | Solicita relatório assíncrono via RabbitMQ |
| `GET` | `/api/relatorios/{protocolo}/status` | Consulta status do protocolo assíncrono |
| `GET` | `/api/relatorios/{protocolo}/download` | Baixa o arquivo processado pelo protocolo |
| `GET` | `/api/relatorios/painel` | Painel web interativo para testes de relatórios |

---

## ⚙️ Variáveis de Ambiente (.env)

Crie um arquivo `.env` na raiz do projeto ou configure as variáveis no ambiente:

```env
# Banco de Dados PostgreSQL
DB_URL2=jdbc:postgresql://localhost:5435/gestao_financeira
DB_USER1=postgres
DB_PASSWORD1=sua_senha_aqui

# RabbitMQ (Opcional - desativa listeners automaticamente se não configurado)
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# Segurança JWT
JWT_SECRET=sua_chave_secreta_super_segura_com_ao_menos_256_bits
JWT_EXPIRATION_HOURS=8
JWT_REFRESH_EXPIRATION_DAYS=7
```

---

## 💻 Como Executar Localmente

### Pré-requisitos
- **Java 25** (OpenJDK / Eclipse Adoptium Temurin)
- **Docker** e **Docker Compose**
- **Maven 3.9+** (ou utilize o wrapper `./mvnw` incluso)

### 1. Iniciar os serviços com Docker
Suba os containers do PostgreSQL e do RabbitMQ pré-configurados:
```bash
docker compose up -d meu-postgres meu-rabbitmq
```

### 2. Executar a API
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.
> As migrações do Flyway executarão automaticamente ao iniciar o projeto.

---

## 📖 Documentação OpenAPI / Swagger

Com a aplicação em execução, acesse a documentação interativa dos endpoints:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 🧪 Testes e Cobertura (JaCoCo)

O projeto possui suíte de testes unitários e de integração cobrindo regras de domínio, repositórios, mensageria e relatórios:

```bash
# Executar todos os testes
.\mvnw.cmd test

# Gerar relatório HTML de cobertura com JaCoCo
.\mvnw.cmd verify
```

O relatório visual de cobertura estará disponível em:
`target/site/jacoco/index.html`

---

## 🏷️ Padrão de Commits

O projeto segue as convenções do **Conventional Commits**:

| Prefixo | Finalidade |
|---|---|
| `feat:` | Nova funcionalidade introduzida |
| `fix:` | Correção de bug ou regressão |
| `refactor:` | Refatoração de código sem alteração funcional |
| `ci:` | Alterações em pipelines ou workflows do GitHub Actions |
| `test:` | Adição ou aprimoramento de testes |
| `docs:` | Atualizações de documentação e README |

---

## 👨‍💻 Autor

Desenvolvido por **Tiago Silva Rufiniano**  
- **GitHub:** [@TiagoTSR](https://github.com/TiagoTSR)
- **Repositório do Frontend:** [gestao-financeira-frontAngular](https://github.com/TiagoTSR/gestao-financeira-frontAngular)