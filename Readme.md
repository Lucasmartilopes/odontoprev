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

GitHub Actions (`.github/workflows/ci.yml`): `mvn verify` + **Docker build** (útil como CI espelhado no GitHub).

---

## Sprint 2 — Azure DevOps (passo a passo simples)

A disciplina pede **pipeline CI/CD no Azure DevOps** (não basta só o GitHub). O arquivo principal é **`azure-pipelines.yml`** na raiz do repositório.

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

### Como ativar o **CD** (só depois que ACR + Web App existirem)

1. No Azure DevOps: **Project settings** (engrenagem) → **Service connections** → crie:
   - uma conexão **Azure Resource Manager** (sua subscription Azure);
   - uma conexão **Docker Registry** apontando para o **ACR**.  
2. No pipeline: **Edit** → **Variables** → adicione (nomes **iguais** aos do YAML):

| Nome da variável | Valor (exemplo / o que é) |
|------------------|---------------------------|
| `DeployCDN` | `true` (liga o estágio CD) |
| `azureSubscription` | **nome exato** da service connection ARM que você criou |
| `acrServiceConnection` | **nome exato** da service connection do Docker Registry (ACR) |
| `acrLoginServer` | ex.: `meuacr.azurecr.io` |
| `webAppName` | nome do seu **App Service** Linux (Web App for Containers) |

3. No **Azure Portal** → seu App Service → **Configuration** → **Application settings** → adicione **`NEON_PASSWORD`** com a senha do Neon (igual ao que você já faz no PC).  
4. Rode o pipeline de novo. O CD faz **push** da imagem e atualiza o Web App.

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
4. Abrir a aplicação no ar (URL do App Service ou `localhost`) e fazer um **CRUD** simples.  
5. Abrir o **Neon** → **SQL Editor** → `SELECT * FROM paciente;` (e `consulta`) mostrando os dados **persistidos**.

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
├── azure-pipelines.yml          ← Sprint 2: CI/CD Azure DevOps
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

1. **Azure DevOps**: criar projeto, apontar pipeline para `azure-pipelines.yml`, rodar o pipeline (e configurar CD quando tiver Azure).  
2. **Vídeo Sprint 2**: entrar no **Azure DevOps** → executar pipeline → app funcionando → **dados no banco na nuvem** (Neon com `SELECT`).  
3. **PDF** (item 7): nomes, **RM**, link **GitHub**, link **YouTube**.  
4. Preencher **integrantes** abaixo e o **link do vídeo**.

| Nome | RM | Turma |
|------|-----|--------|
| *(preencher)* | | |
| *(preencher)* | | |

**Vídeo:** *(cole o link público do YouTube/Drive quando estiver pronto)*

---

## Licença

Projeto acadêmico (FIAP) — uso educacional.
