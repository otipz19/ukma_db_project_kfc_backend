CREATE TABLE client_meals (
    id SERIAL PRIMARY KEY,
    energetic_value INTEGER NOT NULL,
    weight INTEGER NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    meal_id INTEGER NOT NULL,
    order_id INTEGER NOT NULL,
    amount_in_order INTEGER NOT NULL,

    FOREIGN KEY (meal_id)
        REFERENCES meals(id)
        ON DELETE NO ACTION,
    FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);
