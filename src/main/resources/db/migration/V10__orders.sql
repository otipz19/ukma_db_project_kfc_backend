CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    date_created TIMESTAMP NOT NULL,
    is_completed BOOLEAN NOT NULL,
    restaurant_id INTEGER NOT NULL REFERENCES restaurants(id) ON DELETE NO ACTION,
    client_user_id INTEGER NULL REFERENCES clients(user_id) ON DELETE SET NULL,
    employee_user_id INTEGER NULL REFERENCES employees(user_id) ON DELETE SET NULL
);