package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity {

    private int id;

    private Integer userId;

    private String username;

    @NotBlank(message = "error.client.surname.blank")
    @Size(min = 1, max = 64, message = "error.client.surname.size")
    private String surname;

    @NotBlank(message = "error.client.first-name.blank")
    @Size(min = 1, max = 64, message = "error.client.first-name.size")
    private String firstName;

    @NotBlank(message = "error.client.middle-name.blank")
    @Size(min = 1, max = 64, message = "error.client.middle-name.size")
    private String middleName;

    @PositiveOrZero(message = "error.client.bonuses.min")
    private int bonuses;

    @Past(message = "error.client.birth-date.future")
    private LocalDate birthDate;

    private boolean isDeleted;
}
