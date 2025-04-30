package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientEntity {

    private Integer id;

    @NotBlank(message = "error.ingredient.title.blank")
    @Size(max = 64, message = "error.ingredient.title.size")
    private String title;

    @Positive(message = "error.ingredient.energetic-value.min")
    private int energeticValue;

    @Positive(message = "error.ingredient.weight.min")
    private int weight;

    @NotNull(message = "error.ingredient.price.null")
    @DecimalMin(value = "0.1", message = "error.ingredient.price.min")
    private BigDecimal price;

    private boolean isActual = true;

    public IngredientEntity copy() {
        return new IngredientEntity(id, title, energeticValue, weight, price, isActual);
    }
}