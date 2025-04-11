package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.MealRepository;

@ApplicationScoped
public class MealValidator extends BaseValidator<MealEntity> {

    @Inject
    private MealRepository mealRepository;

    @Override
    public void validForCreate(MealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        mealRepository.findByTitle(entity.getTitle())
                .ifPresent(m -> {
                    throw new BadRequestException("Meal with this title already exists");
                });
    }

    @Override
    public void validForUpdate(MealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        boolean titleIsOccupied = mealRepository.findByTitle(entity.getTitle())
                .map(m -> m.getId() != entity.getId())
                .orElse(false);
        if (titleIsOccupied)
            throw new BadRequestException("Meal with this title already exists");
    }

    @Override
    public void validForDelete(MealEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}
