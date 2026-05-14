CREATE SEQUENCE IF NOT EXISTS credit_evaluations_SEQ START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS credit_evaluations (
    id BIGSERIAL PRIMARY KEY,
    cedula VARCHAR(10) NOT NULL,
    monto_solicitado NUMERIC(15,2) NOT NULL,
    plazo_anios INTEGER NOT NULL,
    salario NUMERIC(15,2) NOT NULL,
    score_obtenido INTEGER,
    deuda_mensual NUMERIC(15,2),
    estado VARCHAR(20) NOT NULL,
    fecha_evaluacion TIMESTAMP NOT NULL DEFAULT NOW(),
    motivo_rechazo VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_credit_evaluations_cedula ON credit_evaluations(cedula);
CREATE INDEX IF NOT EXISTS idx_credit_evaluations_fecha ON credit_evaluations(fecha_evaluacion DESC);
