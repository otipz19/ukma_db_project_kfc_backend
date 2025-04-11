package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealIngredientEntity {

    @NotNull
    private int mealId;

    @NotNull
    private int ingredientId;

    @Positive
    private int amount;

    @NotNull
    private boolean isFixated;
}
