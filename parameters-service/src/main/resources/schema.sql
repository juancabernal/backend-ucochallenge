CREATE TABLE IF NOT EXISTS parameters (
    id UUID PRIMARY KEY,
    param_key TEXT NOT NULL UNIQUE,
    value TEXT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_parameters_key ON parameters(param_key);
