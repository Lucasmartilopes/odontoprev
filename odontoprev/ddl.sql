-- =============================================================================
-- OdontoPrev — DDL PostgreSQL (entrega acadêmica)
-- Tabelas: paciente, consulta (1:N)
-- =============================================================================

CREATE TABLE paciente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL,
    telefone VARCHAR(20),
    data_nascimento DATE NOT NULL
);

CREATE TABLE consulta (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    procedimento VARCHAR(120) NOT NULL,
    dentista VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (paciente_id) REFERENCES paciente(id) ON DELETE CASCADE,
    CONSTRAINT chk_consulta_status
        CHECK (status IN ('AGENDADA', 'REALIZADA', 'CANCELADA'))
);

CREATE INDEX idx_consulta_paciente ON consulta(paciente_id);

-- ---------------------------------------------------------------------------
-- Comentários (metadados no dicionário de dados)
-- ---------------------------------------------------------------------------
COMMENT ON TABLE paciente IS 'Cadastro de pacientes da clínica odontológica.';
COMMENT ON COLUMN paciente.id IS 'Identificador interno (surrogate key).';
COMMENT ON COLUMN paciente.nome IS 'Nome completo do paciente.';
COMMENT ON COLUMN paciente.cpf IS 'CPF único do paciente (documento).';
COMMENT ON COLUMN paciente.email IS 'E-mail de contato.';
COMMENT ON COLUMN paciente.telefone IS 'Telefone de contato (opcional).';
COMMENT ON COLUMN paciente.data_nascimento IS 'Data de nascimento.';

COMMENT ON TABLE consulta IS 'Agenda de consultas vinculadas a um paciente.';
COMMENT ON COLUMN consulta.id IS 'Identificador da consulta.';
COMMENT ON COLUMN consulta.paciente_id IS 'FK para paciente (dono do relacionamento).';
COMMENT ON COLUMN consulta.data_hora IS 'Data e hora agendadas da consulta.';
COMMENT ON COLUMN consulta.procedimento IS 'Descrição do procedimento odontológico.';
COMMENT ON COLUMN consulta.dentista IS 'Nome do profissional responsável.';
COMMENT ON COLUMN consulta.status IS 'Situação: AGENDADA, REALIZADA ou CANCELADA.';
