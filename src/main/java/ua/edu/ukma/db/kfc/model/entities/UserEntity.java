package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    private int id;

    @NotBlank
    @Size(min = 1, max = 320)
    private String username;

    @NotBlank
    @Size(min = 1, max = 250)
    private String passwordHash;

    @NotNull
    private RoleEnum role;

    private boolean isActive;
}
