package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;

import java.util.List;

@ApplicationScoped
public class ClientValidator extends BaseValidator<ClientEntity> {

    @Override
    public void validForView(ClientEntity entity) {
        if (actionForThemself(entity)) return;
        securityContextHolder.requireRole(UserRoleEnum.ADMIN, UserRoleEnum.MANAGER, UserRoleEnum.CASHIER);
    }

    @Override
    public void validForView(List<ClientEntity> userEntities) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN, UserRoleEnum.MANAGER, UserRoleEnum.CASHIER);
    }

    @Override
    public void validForCreate(ClientEntity entity) {
        validateData(entity);
    }

    @Override
    public void validForUpdate(ClientEntity entity) {
        if (actionForThemself(entity)) return;
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
    }

    @Override
    public void validForDelete(ClientEntity entity) {
        if (actionForThemself(entity)) return;
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }

    private boolean actionForThemself(ClientEntity entity) {
        String currentUser = securityContextHolder.getContext().getUsername();
        return currentUser.equals(entity.getUsername());
    }
}
