BEGIN;

CREATE TABLE AppUser (
                         id SERIAL PRIMARY KEY,
                         email VARCHAR(320) NOT NULL UNIQUE,
                         password_hash VARCHAR(250) NOT NULL
);

CREATE TABLE Client (
                        id SERIAL PRIMARY KEY,
                        surname VARCHAR(64) NOT NULL,
                        first_name VARCHAR(64) NOT NULL,
                        middle_name VARCHAR(64) NULL,
                        bonuses INTEGER NOT NULL,
                        birth_date DATE NULL,
                        is_deleted BOOL NOT NULL DEFAULT FALSE,
                        user_id INTEGER NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES AppUser(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Employee (
                          id SERIAL PRIMARY KEY,
                          passport_number CHAR(14) NOT NULL UNIQUE,
                          surname VARCHAR(64) NOT NULL,
                          first_name VARCHAR(64) NOT NULL,
                          middle_name VARCHAR(64) NULL,
                          salary MONEY NOT NULL,
                          birth_date DATE NOT NULL,
                          position VARCHAR(64) NOT NULL,
                          is_deleted BOOL NOT NULL DEFAULT FALSE,
                          manager_id INTEGER NULL,
                          user_id INTEGER NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES AppUser(id) ON UPDATE CASCADE ON DELETE CASCADE,
                          FOREIGN KEY (manager_id) REFERENCES Employee(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

COMMIT;