package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.ClientMealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;

import java.util.List;

@ApplicationScoped
public class ClientMealIngredientValidator extends BaseValidator<ClientMealIngredientEntity> {

    @Inject
    private IngredientRepository ingredientRepository;

    @Override
    public void validForCreate(ClientMealIngredientEntity entity) {
        throw new ForbiddenException(); // only batch creation is allowed
    }

    public void validForCreate(List<ClientMealIngredientEntity> entities) {
        entities.forEach(this::validateData);
        if (entities.isEmpty())
            throw new ValidationException("error.modify-meal.no-ingredients");
        if (!ingredientRepository.checkAllAreActualByIds(entities.stream().map(ClientMealIngredientEntity::getIngredientId).toList()))
            throw new ValidationException("error.modify-meal.ingredient-not-found");
    }

    @Override
    public void validForDelete(ClientMealIngredientEntity entity) {
        throw new ForbiddenException();
    }
}
