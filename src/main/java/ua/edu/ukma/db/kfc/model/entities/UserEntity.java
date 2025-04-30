package ua.edu.ukma.db.kfc.model.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    private Integer id;

    @NotBlank(message = "error.user.username.blank")
    @Size(max = 320, message = "error.user.username.size")
    private String username;

    @NotBlank(message = "error.user.password-hash.blank")
    @Size(max = 250, message = "error.user.password-hash.size")
    private String passwordHash;

    @NotNull(message = "error.user.role.null")
    private UserRoleEnum role;

    private boolean isActive;
}
