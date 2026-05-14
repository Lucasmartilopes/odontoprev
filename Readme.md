# OdontoPrev — gestão de pacientes e consultas

Aplicação **Spring Boot 3** (Java 17) com **Thymeleaf**, **CRUD** de **Paciente** e **Consulta** (1:N), **camada de serviços de aplicação**, API REST em `/api/**`, persistência em **PostgreSQL na nuvem** (ex.: Neon) e documentação para **deploy no Azure**.

> O código Maven fica na pasta **`odontoprev/`** (monorepositório com raiz Git aqui em cima).

---

## Descrição da solução

Sistema web para clínica odontológica: cadastro de pacientes e agenda de consultas com validação (CPF único, status `AGENDADA` / `REALIZADA` / `CANCELADA`). Os **serviços de aplicação** (`PacienteService`, `ConsultaService`) concentram regras de uso dos casos e os controllers (MVC e REST) apenas orquestram HTTP.

---

## Benefícios para o negócio

- Menos erro operacional (validações e dados centralizados).
- Acesso remoto à agenda e ao cadastro (nuvem).
- Histórico de consultas por paciente para continuidade do tratamento.
- Base para evoluir para app mobile ou integrações.

---

## Arquitetura

Diagrama em [`odontoprev/docs/arquitetura_odontoprev_cloud.svg`](odontoprev/docs/arquitetura_odontoprev_cloud.svg).

Fluxo resumido: navegador → Azure App Service (container) → Spring Boot → PostgreSQL na nuvem.

---

## Banco de dados (DDL)

Arquivo de entrega: [`odontoprev/ddl.sql`](odontoprev/ddl.sql) (tabelas, PKs, FK, índice e **comentários** `COMMENT ON`).

Script auxiliar com dados de exemplo: [`odontoprev/docs/script.sql`](odontoprev/docs/script.sql).

**Importante:** em produção **não** use H2. O H2 aparece **somente** no perfil `test` para CI (`src/test/resources/application-test.properties`).

---

## Pré-requisitos

- Java **17**+
- Maven (ou use o wrapper em `odontoprev/mvnw`)
- Conta em provedor **PostgreSQL na nuvem** (Neon, Azure Database for PostgreSQL, etc.)

---

## Configuração (só a senha)

A **URL** e o **usuário** do Neon já estão em `odontoprev/src/main/resources/application.properties`.  
Você **não** coloca senha no Git: use a variável **`NEON_PASSWORD`** (local ou Azure).

Texto de apoio: [`odontoprev/docs/env-exemplo.txt`](odontoprev/docs/env-exemplo.txt).

| Onde | O que fazer |
|------|-------------|
| Seu PC (PowerShell) | `$env:NEON_PASSWORD="sua_senha"` antes de rodar o `mvnw` |
| Azure App Service | **Configuration** → criar `NEON_PASSWORD` = sua senha |

### PowerShell (sessão atual)

```powershell
cd odontoprev
$env:NEON_PASSWORD="SUA_SENHA_DO_NEON"
.\mvnw.cmd spring-boot:run
```

### Bash

```bash
cd odontoprev
export NEON_PASSWORD="SUA_SENHA_DO_NEON"
./mvnw spring-boot:run
```

Aplicação: **http://localhost:8080** · Swagger: **http://localhost:8080/swagger-ui/index.html**

---

## Testes e build (CI)

Na pasta `odontoprev`:

```powershell
.\mvnw.cmd -B verify
```

GitHub Actions (`.github/workflows/ci.yml`): `mvn verify` + **Docker build**. O push da imagem para ACR e o deploy no App Service exigem **secrets** da sua conta Azure (configure conforme seu laboratório).

---

## Docker

```powershell
cd odontoprev
docker build -t odontoprev:latest .
```

No Azure, defina **`NEON_PASSWORD`** nas **Application settings** da Web App (URL e usuário já vêm do `application.properties` embutido no JAR).

---

## Rotas — interface web (Thymeleaf)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/` | Início |
| GET | `/pacientes` | Lista pacientes |
| GET | `/pacientes/novo` | Novo paciente |
| POST | `/pacientes` | Salva paciente |
| GET | `/pacientes/editar/{id}` | Edição |
| GET | `/pacientes/excluir/{id}` | Exclui |
| GET | `/consultas` | Lista consultas |
| GET | `/consultas/nova` | Nova consulta |
| POST | `/consultas` | Salva consulta |
| GET | `/consultas/editar/{id}` | Edição |
| GET | `/consultas/excluir/{id}` | Exclui |

---

## API REST + JSON (testes do professor)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/pacientes` | Lista |
| GET | `/api/pacientes/{id}` | Detalhe |
| POST | `/api/pacientes` | Cria (JSON corpo) |
| PUT | `/api/pacientes/{id}` | Atualiza |
| DELETE | `/api/pacientes/{id}` | Remove |
| GET | `/api/consultas` | Lista (com nome do paciente) |
| GET | `/api/consultas/{id}` | Detalhe |
| POST | `/api/consultas` | Cria |
| PUT | `/api/consultas/{id}` | Atualiza |
| DELETE | `/api/consultas/{id}` | Remove |

Exemplos de corpo JSON: [`odontoprev/docs/crud-exemplos.json`](odontoprev/docs/crud-exemplos.json)  
Requisições prontas (REST Client): [`odontoprev/docs/api-crud.http`](odontoprev/docs/api-crud.http)

---

## Estrutura do repositório

```
.
├── .github/workflows/ci.yml
├── Readme.md
└── odontoprev/
    ├── Dockerfile
    ├── ddl.sql
    ├── docs/
    ├── pom.xml
    └── src/
```

---

## O que ainda é manual (entrega acadêmica)

1. **Vídeo** (720p, áudio ou legenda): clone ou build da imagem → deploy Azure → CRUD na aplicação → dados visíveis no banco em nuvem.  
2. **PDF** com nomes, RMs, link do GitHub e link do vídeo (acesso liberado ao professor).  
3. Preencher **integrantes** abaixo e o **link do vídeo**.

| Nome | RM | Turma |
|------|-----|--------|
| *(preencher)* | | |
| *(preencher)* | | |

**Vídeo:** *(cole o link público do YouTube/Drive quando estiver pronto)*

---

## Licença

Projeto acadêmico (FIAP) — uso educacional.
