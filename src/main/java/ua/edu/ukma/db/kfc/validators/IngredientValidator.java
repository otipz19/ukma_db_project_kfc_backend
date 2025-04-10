package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;

import java.util.Objects;

@ApplicationScoped
public class IngredientValidator extends BaseValidator<IngredientEntity>{

    @Inject
    private IngredientRepository ingredientRepository;

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
        validateTitle(entity);
    }

    private void validateTitle(IngredientEntity entity) {
        boolean titleIsOccupied = ingredientRepository.findByTitle(entity.getTitle())
                .map(i -> !Objects.equals(i.getId(), entity.getId()))
                .orElse(false);
        if (titleIsOccupied)
            throw new ValidationException("error.ingredient.title.duplicate");
    }

    @Override
    public void validForDelete(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}