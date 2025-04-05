package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;

@ApplicationScoped
public class UserValidator extends BaseValidator<UserEntity> {

    public void validForDisableUser(UserEntity user) {
        securityContextHolder.requireRole(RoleEnum.ADMIN);
        if (user.getRole().equals(RoleEnum.ADMIN))
            throw new BadRequestException("Cannot disable an admin");
    }
}
