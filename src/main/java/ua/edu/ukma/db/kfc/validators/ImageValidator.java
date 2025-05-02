package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.ukma.db.kfc.model.entities.ImageEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;

@ApplicationScoped
public class ImageValidator extends BaseValidator<ImageEntity> {

    @Override
    public void validForCreate(ImageEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
    }

    @Override
    public void validForUpdate(ImageEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
    }

    @Override
    public void validForDelete(ImageEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}
