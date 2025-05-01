package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.MealRepository;

import java.util.Objects;

@ApplicationScoped
public class MealValidator extends BaseValidator<MealEntity> {

    @Inject
    private MealRepository mealRepository;

    @Override
    public void validForCreate(MealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        validateTitle(entity);
    }

    @Override
    public void validForUpdate(MealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
    }

    private void validateTitle(MealEntity entity) {
        if (mealRepository.existsByTitle(entity.getTitle()))
            throw new ValidationException("error.meal.title.duplicate");
    }

    @Override
    public void validForDelete(MealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}
