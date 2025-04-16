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
    @Size(min = 1, max = 64, message = "error.meal.title.size")
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

    private int energeticValue;

    private int weight;

    private BigDecimal price;

    private boolean isActual = true;
}
