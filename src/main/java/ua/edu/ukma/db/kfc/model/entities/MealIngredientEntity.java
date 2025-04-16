package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealIngredientEntity {

    private Integer mealId;

    private Integer ingredientId;

    @Positive(message = "error.meal-ingredient.amount.min")
    private int amount;

    private boolean isFixated;
}
