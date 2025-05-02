package ua.edu.ukma.db.kfc.model.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantStatistic {

    private int id;

    private String address;

    private Integer managerUserId;

    private String managerPassportNumber;

    private String managerSurname;

    private int numberOfOrders;

    private int totalOrdersPrice;

    private boolean isDeleted;
}
