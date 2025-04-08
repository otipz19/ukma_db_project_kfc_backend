CREATE TABLE meal (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64) NOT NULL UNIQUE,
    additional_price NUMERIC(10, 2) NOT NULL,
    description VARCHAR(512) NOT NULL,
    recipe VARCHAR(1024) NOT NULL,
    energetic_value INTEGER NOT NULL,
    weight INTEGER NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
