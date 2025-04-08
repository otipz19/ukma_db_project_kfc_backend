package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.RestaurantRepository;

@ApplicationScoped
public class RestaurantValidator extends BaseValidator<RestaurantEntity> {

    @Inject
    private RestaurantRepository restaurantRepository;

    @Override
    public void validForCreate(RestaurantEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        if (restaurantRepository.findIdByAddress(entity.getAddress()).isPresent())
            throw new BadRequestException("Restaurant with this address already exists");
    }

    @Override
    public void validForUpdate(RestaurantEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        boolean addressIsOccupied = restaurantRepository.findIdByAddress(entity.getAddress())
                .map(id -> id != entity.getId())
                .orElse(false);
        if (addressIsOccupied)
            throw new BadRequestException("Restaurant with this address already exists");
    }

    @Override
    public void validForDelete(RestaurantEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
    }
}
