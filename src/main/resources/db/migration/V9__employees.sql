CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    passport_number CHAR(14) NOT NULL,
    surname VARCHAR(64) NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    middle_name VARCHAR(64) NULL,
    salary NUMERIC(10, 2) NOT NULL,
    birth_date DATE NOT NULL,
    manager_id INTEGER NULL REFERENCES employees(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    restaurant_id INTEGER NULL REFERENCES restaurants(id) ON UPDATE CASCADE ON DELETE NO ACTION,
    user_id INTEGER NULL UNIQUE REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE employees
ADD CONSTRAINT employees_unique_passport_number EXCLUDE USING GIST (passport_number WITH =)
WHERE (is_deleted = FALSE);

INSERT INTO employees (id, passport_number, surname, first_name, salary, birth_date, user_id) VALUES
(0, '00000000000000', 'Top', 'Manager', 0, '2000-01-01', 0);