package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.model.entities.OrderEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.EmployeeRepository;
import ua.edu.ukma.db.kfc.repositories.RestaurantRepository;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class OrderValidator extends BaseValidator<OrderEntity> {

    @Inject
    private RestaurantRepository restaurantRepository;
    @Inject
    private EmployeeRepository employeeRepository;

    @Override
    public void validForView(OrderEntity entity) {
        if (!canView(entity))
            throw new ForbiddenException();
    }

    @Override
    public void validForView(List<OrderEntity> entities) {
        entities.forEach(this::validForView);
    }

    private boolean canView(OrderEntity entity) {
        return switch (securityContextHolder.getContext().getUserRole()) {
            case ADMIN -> true;
            case CLIENT -> clientCanView(entity);
            case MANAGER, COOK, CASHIER -> employeeCanInteract(entity);
        };
    }

    private boolean clientCanView(OrderEntity entity) {
        ClientEntity currentClient = securityContextHolder.getCurrentClientOrThrow();
        return Objects.equals(entity.getClientId(), currentClient.getId());
    }

    private boolean employeeCanInteract(OrderEntity entity) {
        EmployeeEntity currentEmployee = securityContextHolder.getCurrentEmployeeOrThrow();
        return Objects.equals(entity.getRestaurantId(), currentEmployee.getRestaurantId());
    }

    @Override
    public void validForCreate(OrderEntity entity) {
         super.validForCreate(entity);
         if (!restaurantRepository.existsById(entity.getRestaurantId()))
             throw new ValidationException("error.create-order.restaurant.not-exists");
         if (entity.getEmployeeUserId() != null) {
             EmployeeEntity employee = employeeRepository.findByUserId(entity.getEmployeeUserId())
                     .orElseThrow(() -> new ValidationException("error.create-order.employee.not-exists"));
             if (!Objects.equals(employee.getRestaurantId(), entity.getRestaurantId()))
                 throw new ValidationException("error.create-order.employee.different-restaurant");
         }
    }

    public void validForComplete(OrderEntity entity) {
        if (securityContextHolder.hasRole(UserRoleEnum.ADMIN)) return;
        if (!employeeCanInteract(entity))
            throw new ForbiddenException();
    }
}
