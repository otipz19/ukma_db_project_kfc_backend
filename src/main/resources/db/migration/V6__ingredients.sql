CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64) NOT NULL,
    energetic_value INTEGER NOT NULL,
    weight INTEGER NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    is_actual BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE ingredients
ADD CONSTRAINT ingredients_unique_title EXCLUDE USING GIST (title WITH =)
WHERE (is_actual = TRUE);