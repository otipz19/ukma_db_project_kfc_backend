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
    @Size(min = 1, max = 64, message = "error.ingredient.title.size")
    private String title;

    @PositiveOrZero(message = "error.ingredient.energetic-value.min")
    private int energeticValue;

    @PositiveOrZero(message = "error.ingredient.weight.min")
    private int weight;

    @NotNull(message = "error.ingredient.price.null")
    @DecimalMin(value = "0.0", message = "error.ingredient.price.min")
    private BigDecimal price;

    private boolean isActual = true;
}