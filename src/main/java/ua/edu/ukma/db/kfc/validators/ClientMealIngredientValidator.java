package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import ua.edu.ukma.db.kfc.model.entities.ClientMealIngredientEntity;

import java.util.List;

@ApplicationScoped
public class ClientMealIngredientValidator extends BaseValidator<ClientMealIngredientEntity> {

    @Override
    public void validForCreate(ClientMealIngredientEntity entity) {
        throw new ForbiddenException(); // only batch creation is allowed
    }

    public void validForCreate(List<ClientMealIngredientEntity> entities) {
        entities.forEach(this::validateData);
    }

    @Override
    public void validForUpdate(ClientMealIngredientEntity entity) {
        throw new ForbiddenException();
    }

    @Override
    public void validForDelete(ClientMealIngredientEntity entity) {
        throw new ForbiddenException();
    }
}
