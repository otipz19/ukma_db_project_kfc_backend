package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
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
                    throw new BadRequestException("Ingredient with this title already exists");
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
            throw new BadRequestException("Ingredient with this title already exists");
    }

    @Override
    public void validForDelete(IngredientEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}