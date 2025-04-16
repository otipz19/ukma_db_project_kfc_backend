package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;

import java.util.List;

@ApplicationScoped
public class MealIngredientValidator extends BaseValidator<MealIngredientEntity> {

    @Inject
    private IngredientRepository ingredientRepository;

    @Override
    public void validForCreate(MealIngredientEntity entity) {
        throw new ForbiddenException(); // only batch creation is allowed
    }

    public void validForCreate(List<MealIngredientEntity> entities) {
        entities.forEach(this::validateData);
        if (entities.isEmpty())
            throw new ValidationException("error.modify-meal.no-ingredients");
        if (!ingredientRepository.checkAllAreActualByIds(entities.stream().map(MealIngredientEntity::getIngredientId).toList()))
            throw new ValidationException("error.modify-meal.ingredient-not-found");
    }

    @Override
    public void validForUpdate(MealIngredientEntity entity) {
        throw new ForbiddenException();
    }

    @Override
    public void validForDelete(MealIngredientEntity entity) {
        throw new ForbiddenException();
    }
}
