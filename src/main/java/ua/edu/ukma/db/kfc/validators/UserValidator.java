package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.UserRepository;

@ApplicationScoped
public class UserValidator extends BaseValidator<UserEntity> {

    @Inject
    private UserRepository userRepository;

    @Override
    public void validForCreate(UserEntity entity) {
        validateData(entity);
        if (userRepository.existsByEmail(entity.getUsername()))
            throw new BadRequestException("User with this username already exists");
    }

    public void validForDisableUser(UserEntity user) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        if (user.getRole().equals(UserRoleEnum.ADMIN))
            throw new BadRequestException("Cannot disable an admin");
    }
}
