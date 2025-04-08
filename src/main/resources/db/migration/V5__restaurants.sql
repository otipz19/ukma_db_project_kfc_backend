CREATE TABLE restaurants (
    id SERIAL PRIMARY KEY,
    address VARCHAR(320) NOT NULL,
    is_deleted BOOL NOT NULL DEFAULT FALSE
);

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE restaurants
ADD CONSTRAINT restaurants_unique_address EXCLUDE USING GIST (address WITH =)
WHERE (is_deleted = FALSE);