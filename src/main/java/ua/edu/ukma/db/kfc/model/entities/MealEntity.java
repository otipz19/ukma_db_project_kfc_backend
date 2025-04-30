package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealEntity {

    private Integer id;

    @NotBlank(message = "error.meal.title.blank")
    @Size(max = 64, message = "error.meal.title.size")
    private String title;

    @NotNull(message = "error.meal.additional-price.null")
    @DecimalMin(value = "0.0", message = "error.meal.additional-price.min")
    private BigDecimal additionalPrice;

    @NotBlank(message = "error.meal.description.blank")
    @Size(max = 512, message = "error.meal.description.size")
    private String description;

    @NotBlank(message = "error.meal.recipe.blank")
    @Size(max = 1024, message = "error.meal.recipe.size")
    private String recipe;

    @Positive(message = "error.meal.energetic-value.min")
    private int energeticValue;

    @Positive(message = "error.meal.weight.min")
    private int weight;

    @DecimalMin(value = "0.1", message = "error.meal.price.min")
    private BigDecimal price;

    private boolean isActual = true;
}
