package ua.edu.ukma.db.kfc.model.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.db.kfc.model.enums.EmployeePositionEnum;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeStatistic {

    private int userId;

    private String passportNumber;

    private String surname;

    private EmployeePositionEnum position;

    private Integer restaurantId;

    private String restaurantAddress;

    private int numberOfOrders;

    private BigDecimal totalOrdersPrice;
}
