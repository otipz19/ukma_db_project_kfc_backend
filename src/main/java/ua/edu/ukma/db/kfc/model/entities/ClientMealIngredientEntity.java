package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientMealIngredientEntity {

    private Integer clientMealId;

    private Integer ingredientId;

    @PositiveOrZero(message = "error.client-meal-ingredient.amount.min")
    private int amount;
}