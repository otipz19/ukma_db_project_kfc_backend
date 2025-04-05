CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(250) NOT NULL,
    role VARCHAR(15) NOT NULL,
    is_active BOOL NOT NULL DEFAULT TRUE
);

INSERT INTO users(username, password_hash, role) VALUES
('admin@kfc.com', '0', 'ADMIN');