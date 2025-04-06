package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;

@ApplicationScoped
public class IngredientValidator extends BaseValidator<IngredientEntity>{

    @Inject
    private IngredientRepository ingredientRepository;

    public void validate(IngredientEntity entity) {
        validateData(entity);
    }

    public void createValidate(IngredientEntity entity) {
        validateData(entity);
        ingredientRepository.findByTitle(entity.getTitle())
                .ifPresent(i -> {
                    throw new BadRequestException("Ingredient with this title already exists");
                });
    }
}