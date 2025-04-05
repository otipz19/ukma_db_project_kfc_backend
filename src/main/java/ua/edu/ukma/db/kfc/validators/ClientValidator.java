package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;

import java.util.List;

@ApplicationScoped
public class ClientValidator extends BaseValidator<ClientEntity> {

    @Override
    public void validForView(ClientEntity entity) {
        String currentUser = securityContextHolder.getContext().getUsername();
        if (currentUser.equals(entity.getUsername()))
            return;
        securityContextHolder.requireRole(RoleEnum.ADMIN, RoleEnum.MANAGER);
    }

    @Override
    public void validForView(List<ClientEntity> userEntities) {
        securityContextHolder.requireRole(RoleEnum.ADMIN, RoleEnum.MANAGER);
    }

    @Override
    public void validForCreate(ClientEntity entity) {
        validateData(entity);
    }
}
