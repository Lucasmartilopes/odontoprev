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
| Azure App Service | **Variáveis de ambiente** → `NEON_PASSWORD`, `SPRING_DATASOURCE_PASSWORD` (mesma senha), `WEBSITES_PORT` = `8080`. Se o app usar imagem em **ACR privado**, adicione `DOCKER_REGISTRY_SERVER_URL`, `DOCKER_REGISTRY_SERVER_USERNAME` e `DOCKER_REGISTRY_SERVER_PASSWORD` (valores em **Chaves de acesso** do Container Registry). |

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

GitHub Actions (`.github/workflows/ci.yml`): `mvn verify` + **Docker build** (útil como CI espelhado no GitHub).

---

## Sprint 2 — Azure DevOps (passo a passo simples)

A disciplina pede **pipeline CI/CD no Azure DevOps** (não basta só o GitHub). O arquivo principal é **`azure-pipelines.yml`** (só **CI** — build e testes).  
O deploy (**CD**) fica em **`azure-pipelines-cd.yml`** — use **depois** de criar as Service connections (segundo pipeline no DevOps).

### O que você precisa ter

1. Conta no **Azure DevOps**: [https://dev.azure.com](https://dev.azure.com) (pode usar a mesma conta Microsoft do Azure).  
2. Seu código no **GitHub** (já está).  
3. (Opcional para o estágio **CD**) Conta **Azure** com App Service + ACR + *service connections* — só quando for gravar deploy automático.

### Passos no Azure DevOps (faça nesta ordem)

1. Entre em **dev.azure.com** e crie uma **Organization** (se ainda não tiver).  
2. Dentro dela, crie um **Project** (ex.: `odontoprev`).  
3. No menu esquerdo: **Pipelines** → **New pipeline**.  
4. Escolha **GitHub** (autorize o GitHub se pedir) e selecione o repositório **odontoprev**.  
5. Quando perguntar o tipo: **Existing Azure Pipelines YAML file**.  
6. Branch **main** e caminho do arquivo: **`/azure-pipelines.yml`** → **Continue** → **Save and run**.  
7. Espere o pipeline **CI** terminar em verde (Maven + Docker build).

### Como “rodar de novo” o pipeline (para vídeo ou professor)

1. **Pipelines** → clique no nome do pipeline.  
2. Botão **Run pipeline** (canto superior direito) → **Run**.

### Como rodar o **CD** (deploy no Azure — segundo pipeline)

1. **Project settings** → **Service connections**: crie **Azure Resource Manager** e **Docker Registry** (ACR), com permissão para os pipelines.  
2. **Pipelines** → **New pipeline** → mesmo repositório GitHub → YAML **`/azure-pipelines-cd.yml`** (arquivo **diferente** do CI).  
3. No pipeline de CD: **Edit** → **Variables** → crie **exatamente** estas 4 (os nomes são os do YAML):

| Nome da variável | Valor (exemplo) |
|------------------|-----------------|
| `azureSubscription` | Nome da service connection ARM (ex.: `sc-azure-odontoprev`) |
| `acrServiceConnection` | Nome da service connection do ACR (ex.: `sc-acr-odontoprev`) |
| `acrLoginServer` | Servidor do ACR (ex.: `acrodontoprevlucas.azurecr.io`) |
| `webAppName` | Nome do Web App (ex.: `odontoprev-lucas123`) |

4. No **Azure Portal** → Web App → **Variáveis de ambiente**: senha do Neon + porta + credenciais do ACR (ver tabela na seção “Configuração”).  
5. **Run pipeline** no pipeline de CD e aguarde concluir em verde.

> **Nota:** o pipeline **`azure-pipelines.yml`** contém **apenas CI** (não use variável `DeployCDN` nele — isso era de uma versão antiga).

### Desenho + dissertação das etapas (PDF / entrega)

Use o arquivo **[`docs/sprint2-pipeline-design.md`](docs/sprint2-pipeline-design.md)** (diagrama + texto por etapa). Copie para o PDF se o professor pedir “dissertação”.

### Como o professor roda os **testes** no próprio PC (sem pipeline)

Na pasta `odontoprev`:

```powershell
.\mvnw.cmd -B verify
```

(Linux/macOS: `bash ./mvnw -B verify`.)

### O que gravar no **vídeo** (Sprint 2)

1. Entrar no **Azure DevOps** e mostrar o projeto.  
2. **Pipelines** → **Run pipeline** → mostrar o **CI** executando (Maven + Docker).  
3. Se o **CD** estiver configurado, mostrar o estágio CD concluindo.  
4. Abrir a aplicação **na URL do Azure App Service** (não use localhost se o professor exigir nuvem) e fazer um **CRUD** simples.  
5. Abrir o **Neon** → **SQL Editor** → `SELECT * FROM paciente;` (e `consulta`) mostrando os dados **persistidos**.

---

## Docker

```powershell
cd odontoprev
docker build -t odontoprev:latest .
```

No Azure, defina **`NEON_PASSWORD`**, **`SPRING_DATASOURCE_PASSWORD`** (mesma senha), **`WEBSITES_PORT`** = `8080` e, se usar ACR privado, as variáveis **`DOCKER_REGISTRY_SERVER_*`** (veja tabela acima). A URL JDBC e o usuário Neon vêm do `application.properties` no JAR.

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
├── azure-pipelines.yml          ← Sprint 2: só CI (Maven + Docker build)
├── azure-pipelines-cd.yml       ← Sprint 2: CD (push ACR + deploy App Service)
├── .github/workflows/ci.yml     ← CI espelhado no GitHub (opcional)
├── docs/
│   └── sprint2-pipeline-design.md  ← Desenho + dissertação das etapas
├── Readme.md
└── odontoprev/
    ├── Dockerfile
    ├── ddl.sql
    ├── docs/
    ├── pom.xml
    └── src/
```

---

## O que ainda é manual (entrega acadêmica — Sprint 1 + 2)

1. **Vídeo**: Azure DevOps (pipelines + **Run**) → site no **Azure** → CRUD → Neon com `SELECT` · **720p** + áudio ou legenda.  
2. **PDF**: nomes, **RM**, link **GitHub**, link **YouTube** (acesso ao professor).  
3. Preencher **integrantes** e **link do vídeo** na tabela abaixo.

| Nome | RM | Turma |
|------|-----|--------|
| *(preencher)* | | |
| *(preencher)* | | |

**Vídeo:** *(cole o link público do YouTube/Drive quando estiver pronto)*

---

## Licença

Projeto acadêmico (FIAP) — uso educacional.
