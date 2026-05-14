-- ============================================
-- DDL OdontoPrev - PostgreSQL (Neon)
-- ============================================

DROP TABLE IF EXISTS consulta CASCADE;
DROP TABLE IF EXISTS paciente CASCADE;

-- Tabela: paciente
CREATE TABLE paciente (
    id               BIGSERIAL    PRIMARY KEY,
    nome             VARCHAR(120) NOT NULL,
    cpf              VARCHAR(14)  NOT NULL UNIQUE,
    email            VARCHAR(120) NOT NULL,
    telefone         VARCHAR(20),
    data_nascimento  DATE         NOT NULL
);

-- Tabela: consulta
CREATE TABLE consulta (
    id            BIGSERIAL     PRIMARY KEY,
    paciente_id   BIGINT        NOT NULL,
    data_hora     TIMESTAMP     NOT NULL,
    procedimento  VARCHAR(120)  NOT NULL,
    dentista      VARCHAR(120)  NOT NULL,
    status        VARCHAR(20)   NOT NULL,
    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_consulta_status
        CHECK (status IN ('AGENDADA', 'REALIZADA', 'CANCELADA'))
);

-- Índices auxiliares
CREATE INDEX idx_consulta_paciente   ON consulta(paciente_id);
CREATE INDEX idx_consulta_data_hora  ON consulta(data_hora);
CREATE INDEX idx_paciente_cpf        ON paciente(cpf);

-- ============================================
-- DML - Dados de exemplo (mínimo 5 registros)
-- ============================================

INSERT INTO paciente (nome, cpf, email, telefone, data_nascimento) VALUES
('Ana Souza',         '111.222.333-44', 'ana@email.com',     '(11) 91111-1111', '1990-05-12'),
('Bruno Lima',        '222.333.444-55', 'bruno@email.com',   '(11) 92222-2222', '1985-08-23'),
('Carla Mendes',      '333.444.555-66', 'carla@email.com',   '(11) 93333-3333', '1995-02-10'),
('Diego Ferreira',    '444.555.666-77', 'diego@email.com',   '(11) 94444-4444', '1988-11-30'),
('Eduarda Ribeiro',   '555.666.777-88', 'eduarda@email.com', '(11) 95555-5555', '2000-07-19');

INSERT INTO consulta (paciente_id, data_hora, procedimento, dentista, status) VALUES
(1, '2026-05-20 09:00:00', 'Limpeza',         'Dr. Marcos',  'AGENDADA'),
(2, '2026-05-21 14:30:00', 'Canal',           'Dra. Paula',  'AGENDADA'),
(3, '2026-05-15 10:00:00', 'Avaliação',       'Dr. Marcos',  'REALIZADA'),
(4, '2026-05-18 16:00:00', 'Extração',        'Dra. Paula',  'CANCELADA'),
(5, '2026-05-22 11:00:00', 'Clareamento',     'Dr. Rafael',  'AGENDADA');

-- ============================================
-- Conferência
-- ============================================
SELECT p.nome, c.data_hora, c.procedimento, c.status
FROM paciente p
JOIN consulta c ON c.paciente_id = p.id
ORDER BY c.data_hora;
