package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity {

    private int id;

    // from UserEntity
    private String username;

    @NotBlank
    @Size(min = 1, max = 64)
    private String surname;

    @NotBlank
    @Size(min = 1, max = 64)
    private String firstName;

    @Size(min = 1, max = 64)
    private String middleName;

    @Min(0)
    private int bonuses;

    @Past
    private LocalDate birthDate;

    private boolean isDeleted;
}
