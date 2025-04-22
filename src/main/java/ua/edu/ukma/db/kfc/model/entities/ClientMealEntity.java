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

    private int energeticValue;

    private int weight;

    private BigDecimal price;

    @NotNull(message = "error.client-meal.meal-id.null")
    private Integer mealId;

    @NotNull(message = "error.client-meal.order-id.null")
    private Integer orderId;

    @Positive(message = "error.client-meal.amount-in-order.min")
    private Integer amountInOrder;
}
