package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.model.entities.ClientMealEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;

@ApplicationScoped
public class ClientMealValidator extends BaseValidator<ClientMealEntity>{

    @Override
    public void validForCreate(ClientMealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
    }

    @Override
    public void validForDelete(ClientMealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }

}
