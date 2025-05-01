package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.OrderRepository;
import ua.edu.ukma.db.kfc.repositories.RestaurantRepository;

import java.util.Objects;

@ApplicationScoped
public class RestaurantValidator extends BaseValidator<RestaurantEntity> {

    @Inject
    private RestaurantRepository restaurantRepository;
    @Inject
    private OrderRepository orderRepository;

    @Override
    public void validForCreate(RestaurantEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        validateAddress(entity);
    }

    @Override
    public void validForUpdate(RestaurantEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        validateData(entity);
        validateAddress(entity);
    }

    private void validateAddress(RestaurantEntity entity) {
        boolean addressIsOccupied = restaurantRepository.findIdByAddress(entity.getAddress())
                .map(id -> !Objects.equals(id, entity.getId()))
                .orElse(false);
        if (addressIsOccupied)
            throw new ValidationException("error.restaurant.address.duplicate");
    }

    @Override
    public void validForDelete(RestaurantEntity entity) {
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        if (restaurantRepository.hasEmployees(entity.getId()))
            throw new ValidationException("error.delete-restaurant.has-employees");
    }
}
