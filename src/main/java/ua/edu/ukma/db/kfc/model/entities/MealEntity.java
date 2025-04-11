package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealEntity {

    private int id;

    @NotBlank
    @Size(min = 1, max = 64)
    private String title;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal additionalPrice;

    @NotBlank
    @Size(max = 512)
    private String description;

    @NotBlank
    @Size(max = 1024)
    private String recipe;

    @PositiveOrZero
    private int energeticValue;

    @PositiveOrZero
    private int weight;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    private List<MealIngredientEntity> ingredients;

    private boolean isActual = true;
}
