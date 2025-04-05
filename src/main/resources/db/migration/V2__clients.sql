CREATE TABLE clients (
    id INTEGER PRIMARY KEY,
    surname VARCHAR(64) NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    middle_name VARCHAR(64) NULL,
    bonuses INTEGER NOT NULL,
    birth_date DATE NULL,
    is_deleted BOOL NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);