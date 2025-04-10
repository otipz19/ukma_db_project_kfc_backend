package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;

@ApplicationScoped
public class IngredientValidator extends BaseValidator<IngredientEntity>{

    @Inject
    private IngredientRepository ingredientRepository;

    @Override
    public void validForCreate(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        ingredientRepository.findByTitle(entity.getTitle())
                .ifPresent(i -> {
                    throw new ValidationException("error.ingredient.title.duplicate");
                });
    }

    @Override
    public void validForUpdate(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        boolean titleIsOccupied = ingredientRepository.findByTitle(entity.getTitle())
                .map(i -> i.getId() != entity.getId())
                .orElse(false);
        if (titleIsOccupied)
            throw new ValidationException("error.ingredient.title.duplicate");
    }

    @Override
    public void validForDelete(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}