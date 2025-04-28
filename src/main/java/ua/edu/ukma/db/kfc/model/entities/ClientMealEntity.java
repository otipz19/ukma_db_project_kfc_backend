package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientMealEntity {

    private Integer id;

    @Positive(message = "error.client-meal.energetic-value.min")
    private int energeticValue;

    @Positive(message = "error.client-meal.weight.min")
    private int weight;

    @DecimalMin(value = "0.1", message = "error.client-meal.price.min")
    private BigDecimal price;

    private int orderId;

    private int mealId;

    @Positive(message = "error.client-meal.amount-in-order.min")
    private int amountInOrder;
}
