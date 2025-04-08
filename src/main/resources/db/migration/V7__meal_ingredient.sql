CREATE TABLE meal_ingredient (
    meal_id INTEGER NOT NULL,
    ingredient_id INTEGER NOT NULL,
    amount INTEGER NOT NULL,
    is_fixated BOOLEAN NOT NULL,

    PRIMARY KEY (meal_id, ingredient_id),

    FOREIGN KEY (meal_id)
        REFERENCES meal(id)
        ON DELETE CASCADE,

    FOREIGN KEY (ingredient_id)
        REFERENCES ingredient(id)
        ON DELETE NO ACTION
);
