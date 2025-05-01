CREATE TABLE clients (
    user_id INTEGER PRIMARY KEY REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    surname VARCHAR(64) NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    middle_name VARCHAR(64) NULL,
    bonuses INTEGER NOT NULL,
    birth_date DATE NULL
);