package ua.edu.ukma.db.kfc.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeStatsEntity {
    private Integer employeeId;
    private double avgPrice;
    private double avgWeight;
    private double avgEnergeticValue;
}