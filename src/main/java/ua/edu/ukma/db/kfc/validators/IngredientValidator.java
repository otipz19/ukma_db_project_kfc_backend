package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;
import ua.edu.ukma.db.kfc.repositories.MealIngredientRepository;

@ApplicationScoped
public class IngredientValidator extends BaseValidator<IngredientEntity>{

    @Inject
    private IngredientRepository ingredientRepository;
    @Inject
    private MealIngredientRepository mealIngredientRepository;

    @Override
    public void validForCreate(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        validateTitle(entity);
    }

    @Override
    public void validForUpdate(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
    }

    private void validateTitle(IngredientEntity entity) {
        if (ingredientRepository.existsByTitle(entity.getTitle()))
            throw new ValidationException("error.ingredient.title.duplicate");
    }

    @Override
    public void validForDelete(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        if (mealIngredientRepository.existsActualMealByIngredientId(entity.getId()))
            throw new ValidationException("error.delete-ingredient.in-use");
    }
}