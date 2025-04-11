package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.MealIngredientRepository;

@ApplicationScoped
public class MealIngredientValidator extends BaseValidator<MealIngredientEntity> {

    @Inject
    private MealIngredientRepository mealIngredientRepository;

    @Override
    public void validForCreate(MealIngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        mealIngredientRepository.findByMealIdAndIngredientId(entity.getMealId(), entity.getIngredientId())
                .ifPresent(mi -> {
                    throw new BadRequestException("MealIngredient with mealId " + entity.getMealId() +
                            " and ingredientId " + entity.getIngredientId() + " already exists");
                });
    }

    @Override
    public void validForUpdate(MealIngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
    }

    @Override
    public void validForDelete(MealIngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}
