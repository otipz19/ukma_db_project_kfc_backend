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

    private int id;

    @NotBlank
    @Size(min = 1, max = 64)
    private String title;

    @PositiveOrZero
    private int energeticValue;

    @PositiveOrZero
    private int weight;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    private boolean isActual = true;
}