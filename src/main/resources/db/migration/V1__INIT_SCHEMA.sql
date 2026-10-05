
-- ============================================
-- MAIN USER TABLE
-- ============================================

CREATE TABLE IF NOT EXISTS agenda_sms (
    telefono TEXT NOT NULL,
    fecha_envio DATE NOT NULL,
    area TEXT NOT NULL,
    estado TEXT NOT NULL DEFAULT 'Ocupado',
    fecha_carga TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (telefono, fecha_envio)
);

CREATE INDEX IF NOT EXISTS idx_fecha_telefono ON agenda_sms (fecha_envio, telefono);
