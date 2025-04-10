package ua.edu.ukma.db.kfc.model.enums;

import lombok.Getter;

@Getter
public enum EmployeePositionEnum {
    TOP_MANAGER(0),
    MANAGER(1),
    COOK(2),
    CASHIER(2);

    private final int priority;

    EmployeePositionEnum(int priority) {
        this.priority = priority;
    }
}
