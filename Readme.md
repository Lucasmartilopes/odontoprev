# OdontoPrev — gestão de pacientes e consultas

Aplicação **Spring Boot 3** (Java 17) com **Thymeleaf**, **CRUD** de **Paciente** e **Consulta** (1:N), camada de serviços (`PacienteService`, `ConsultaService`), API REST em `/api/**`, PostgreSQL na nuvem (ex.: Neon) e pipelines para build/deploy no Azure.

O código Maven está em **`odontoprev/`**.

**Vídeo (demonstração):** [https://youtu.be/4TQRyn9KSfY](https://youtu.be/4TQRyn9KSfY)

---

## Descrição

Sistema web para clínica odontológica: cadastro de pacientes e agenda de consultas (CPF único, status `AGENDADA` / `REALIZADA` / `CANCELADA`). Controllers MVC e REST orquestram HTTP; regras de caso de uso ficam nos serviços.

---

## Arquitetura

Diagrama: [`odontoprev/docs/arquitetura_odontoprev_cloud.svg`](odontoprev/docs/arquitetura_odontoprev_cloud.svg).

Fluxo: cliente → Azure App Service (container) → Spring Boot → PostgreSQL.

---

## Banco de dados

- DDL: [`odontoprev/ddl.sql`](odontoprev/ddl.sql)
- Dados de exemplo: [`odontoprev/docs/script.sql`](odontoprev/docs/script.sql)

Em produção use PostgreSQL. O perfil **`test`** usa H2 em memória (`src/test/resources/application-test.properties`).

---

## Pré-requisitos

- Java **17**+
- Maven ou `odontoprev/mvnw`
- Instância PostgreSQL (Neon, Azure Database for PostgreSQL, etc.)

---

## Configuração

URL e usuário do banco em `odontoprev/src/main/resources/application.properties`. **Não** commite senha: use **`NEON_PASSWORD`** (e no Azure, também **`SPRING_DATASOURCE_PASSWORD`** com o mesmo valor).

Referência: [`odontoprev/docs/env-exemplo.txt`](odontoprev/docs/env-exemplo.txt).

| Ambiente | Variáveis |
|----------|-----------|
| Local (PowerShell) | `$env:NEON_PASSWORD="..."` antes de `mvnw` |
| Azure App Service | `NEON_PASSWORD`, `SPRING_DATASOURCE_PASSWORD`, `WEBSITES_PORT=8080`. Com imagem em **ACR privado**: `DOCKER_REGISTRY_SERVER_URL`, `DOCKER_REGISTRY_SERVER_USERNAME`, `DOCKER_REGISTRY_SERVER_PASSWORD` |

### Executar localmente

**PowerShell**

```powershell
cd odontoprev
$env:NEON_PASSWORD="SUA_SENHA"
.\mvnw.cmd spring-boot:run
```

**Bash**

```bash
cd odontoprev
export NEON_PASSWORD="SUA_SENHA"
./mvnw spring-boot:run
```

- App: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

---

## Testes e build

Na pasta `odontoprev`:

```powershell
.\mvnw.cmd -B verify
```

CI no GitHub: `.github/workflows/ci.yml` (Maven + build da imagem Docker).

---

## Azure DevOps

Dois YAMLs na raiz do repositório:

| Arquivo | Função |
|---------|--------|
| `azure-pipelines.yml` | CI: Maven + Docker build |
| `azure-pipelines-cd.yml` | CD: push da imagem para ACR + deploy no App Service |

**CI:** em Azure DevOps → Pipelines → New pipeline → GitHub → *Existing Azure Pipelines YAML file* → branch `main` → `/azure-pipelines.yml` → Save and run.

**Reexecutar:** Pipelines → pipeline desejado → **Run pipeline**.

**CD:** novo pipeline apontando para `/azure-pipelines-cd.yml`. Variáveis do pipeline (nomes fixos no YAML):

| Variável | Exemplo |
|----------|---------|
| `azureSubscription` | Nome da service connection ARM |
| `acrServiceConnection` | Nome da service connection Docker/ACR |
| `acrLoginServer` | Ex.: `meuacr.azurecr.io` |
| `webAppName` | Nome do Web App |

Documentação opcional do fluxo: [`docs/sprint2-pipeline-design.md`](docs/sprint2-pipeline-design.md).

---

## Docker

```powershell
cd odontoprev
docker build -t odontoprev:latest .
```

No Azure, configure as variáveis de ambiente da tabela acima; JDBC e usuário vêm do `application.properties` empacotado.

---

## Rotas — interface web (Thymeleaf)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/` | Início |
| GET | `/pacientes` | Lista |
| GET | `/pacientes/novo` | Novo |
| POST | `/pacientes` | Salva |
| GET | `/pacientes/editar/{id}` | Edição |
| GET | `/pacientes/excluir/{id}` | Exclui |
| GET | `/consultas` | Lista |
| GET | `/consultas/nova` | Nova |
| POST | `/consultas` | Salva |
| GET | `/consultas/editar/{id}` | Edição |
| GET | `/consultas/excluir/{id}` | Exclui |

---

## API REST

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/pacientes` | Lista |
| GET | `/api/pacientes/{id}` | Detalhe |
| POST | `/api/pacientes` | Cria |
| PUT | `/api/pacientes/{id}` | Atualiza |
| DELETE | `/api/pacientes/{id}` | Remove |
| GET | `/api/consultas` | Lista |
| GET | `/api/consultas/{id}` | Detalhe |
| POST | `/api/consultas` | Cria |
| PUT | `/api/consultas/{id}` | Atualiza |
| DELETE | `/api/consultas/{id}` | Remove |

Exemplos JSON: [`odontoprev/docs/crud-exemplos.json`](odontoprev/docs/crud-exemplos.json)  
HTTP: [`odontoprev/docs/api-crud.http`](odontoprev/docs/api-crud.http)

---

## Estrutura do repositório

```
.
├── azure-pipelines.yml
├── azure-pipelines-cd.yml
├── .github/workflows/ci.yml
├── docs/
│   └── sprint2-pipeline-design.md
├── Readme.md
└── odontoprev/
    ├── Dockerfile
    ├── ddl.sql
    ├── docs/
    ├── pom.xml
    └── src/
```

---

## Licença

Projeto acadêmico (FIAP) — uso educacional.
