package ua.edu.ukma.db.kfc.model.entities;

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

    private BigDecimal cost;

    private LocalDateTime dateCreated;

    private boolean isCompleted;

    private int restaurantId;

    private Integer clientId;

    private Integer clientUserId;

    private Integer employeeId;

    private Integer employeeUserId;
}
