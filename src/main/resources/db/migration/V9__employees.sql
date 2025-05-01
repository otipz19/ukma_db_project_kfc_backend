CREATE TABLE employees (
    user_id INTEGER PRIMARY KEY REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    passport_number CHAR(14) UNIQUE NOT NULL,
    surname VARCHAR(64) NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    middle_name VARCHAR(64) NULL,
    salary NUMERIC(10, 2) NOT NULL,
    birth_date DATE NOT NULL,
    manager_user_id INTEGER NULL REFERENCES employees(user_id) ON UPDATE CASCADE ON DELETE NO ACTION,
    restaurant_id INTEGER NULL REFERENCES restaurants(id) ON UPDATE CASCADE ON DELETE NO ACTION
);

INSERT INTO employees (user_id, passport_number, surname, first_name, salary, birth_date) VALUES
(0, '00000000000000', 'Top', 'Manager', 0, '2000-01-01');