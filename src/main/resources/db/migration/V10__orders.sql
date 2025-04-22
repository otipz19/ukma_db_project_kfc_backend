CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    cost NUMERIC(10, 2) NOT NULL,
    date_created TIMESTAMP NOT NULL,
    is_completed BOOLEAN NOT NULL,
    restaurant_id INTEGER NOT NULL REFERENCES restaurants(id) ON DELETE NO ACTION,
    client_id INTEGER NULL REFERENCES clients(id) ON DELETE NO ACTION,
    employee_id INTEGER NULL REFERENCES employees(id) ON DELETE NO ACTION
);
