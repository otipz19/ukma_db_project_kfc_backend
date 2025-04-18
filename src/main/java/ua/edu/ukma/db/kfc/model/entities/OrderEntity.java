package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    private Integer id;

    @DecimalMin(value = "0.1", message = "error.order.cost.min")
    private BigDecimal cost;

    private LocalDateTime dateCreated;

    private boolean isCompleted;

    private int restaurantId;

    private Integer clientId;

    private Integer clientUserId;

    private Integer employeeId;

    private Integer employeeUserId;
}
