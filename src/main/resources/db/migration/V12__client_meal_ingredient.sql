CREATE TABLE client_meal_ingredient (
    client_meal_id  INTEGER NOT NULL,
    ingredient_id   INTEGER NOT NULL,
    amount          INTEGER NOT NULL,

    PRIMARY KEY (client_meal_id, ingredient_id),

    FOREIGN KEY (client_meal_id)
        REFERENCES client_meal(id)
        ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(id)
        ON DELETE NO ACTION
);