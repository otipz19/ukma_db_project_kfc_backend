CREATE TABLE client_meal (
    id SERIAL PRIMARY KEY,
    energetic_value INTEGER NOT NULL,
    price INTEGER NOT NULL,
    weight INTEGER NOT NULL,
    meal_id           INTEGER NOT NULL,
    order_id          INTEGER NOT NULL,
    amount_in_order   INTEGER NOT NULL,


    FOREIGN KEY (meal_id)
        REFERENCES meals(id)
        ON DELETE NO ACTION,
    FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);
