package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.db.kfc.model.enums.EmployeePositionEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEntity {

    private Integer userId;

    private String username;

    @NotBlank(message = "error.employee.passport-number.blank")
    @Size(min = 14, max = 14, message = "error.employee.passport-number.size")
    private String passportNumber;

    @NotBlank(message = "error.employee.surname.blank")
    @Size(max = 64, message = "error.employee.surname.size")
    private String surname;

    @NotBlank(message = "error.employee.first-name.blank")
    @Size(max = 64, message = "error.employee.first-name.size")
    private String firstName;

    @Size(max = 64, message = "error.employee.middle-name.size")
    private String middleName;

    @NotNull(message = "error.employee.salary.null")
    @DecimalMin(value = "0.0", message = "error.employee.salary.min")
    private BigDecimal salary;

    @NotNull(message = "error.employee.birth-date.null")
    private LocalDate birthDate;

    @NotNull(message = "error.employee.position.null")
    private EmployeePositionEnum position;

    private Integer managerUserId;

    private Integer restaurantId;
}
