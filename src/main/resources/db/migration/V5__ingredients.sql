CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64) NOT NULL UNIQUE,
    energetic_value INTEGER NOT NULL,
    weight INTEGER NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    is_actual BOOLEAN NOT NULL DEFAULT TRUE
);