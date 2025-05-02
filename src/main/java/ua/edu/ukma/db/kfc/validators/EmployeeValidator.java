package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.model.enums.EmployeePositionEnum;
import ua.edu.ukma.db.kfc.model.helper.EmployeeStatistic;
import ua.edu.ukma.db.kfc.repositories.EmployeeRepository;
import ua.edu.ukma.db.kfc.repositories.RestaurantRepository;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class EmployeeValidator extends BaseValidator<EmployeeEntity> {

    @Inject
    private RestaurantRepository restaurantRepository;
    @Inject
    private EmployeeRepository employeeRepository;

    @Override
    public void validForView(EmployeeEntity entity) {
        if (actionForThemself(entity)) return;
        validatePermissions(entity);
    }

    @Override
    public void validForView(List<EmployeeEntity> entities) {
        EmployeeEntity currentEmployee = securityContextHolder.getCurrentEmployeeOrThrow();
        entities.removeIf(e -> !hasPermission(e, currentEmployee));
    }

    public void validForViewStatistics(List<EmployeeStatistic> statistics) {
        EmployeeEntity currentEmployee = securityContextHolder.getCurrentEmployeeOrThrow();
        statistics.removeIf(e -> !hasPermission(e.getPosition(), e.getRestaurantId(), currentEmployee));
    }

    @Override
    public void validForCreate(EmployeeEntity entity) {
        validatePermissions(entity);
        validateData(entity);
        validateAge(entity);
        validatePassportNumber(entity);
        validateRestaurant(entity);
        validateManager(entity);
    }

    @Override
    public void validForUpdate(EmployeeEntity entity) {
        validatePermissions(entity);
        validateData(entity);
        validateAge(entity);
        validatePassportNumber(entity);
    }

    @Override
    public void validForDelete(EmployeeEntity entity) {
        if (actionForThemself(entity)) throw new ForbiddenException();
        validatePermissions(entity);
        if (employeeRepository.hasSubordinates(entity.getUserId()))
            throw new ValidationException("error.delete-employee.has-subordinates");
    }

    private boolean actionForThemself(EmployeeEntity entity) {
        String currentUser = securityContextHolder.getContext().getUsername();
        return currentUser.equals(entity.getUsername());
    }

    private void validatePermissions(EmployeeEntity entity) {
        EmployeeEntity currentEmployee = securityContextHolder.getCurrentEmployeeOrThrow();
        if (!hasPermission(entity, currentEmployee)) throw new ForbiddenException();
    }

    private boolean hasPermission(EmployeeEntity entity, EmployeeEntity currentEmployee) {
        return hasPermission(entity.getPosition(), entity.getRestaurantId(), currentEmployee);
    }

    private boolean hasPermission(EmployeePositionEnum position, Integer restaurantId, EmployeeEntity currentEmployee) {
        if (currentEmployee.getPosition() == EmployeePositionEnum.TOP_MANAGER) return true;
        if (currentEmployee.getPosition().getPriority() >= position.getPriority())
            return false;
        return Objects.equals(currentEmployee.getRestaurantId(), restaurantId);
    }

    private void validateAge(EmployeeEntity entity) {
        if (entity.getBirthDate().plusYears(18).isAfter(TimeUtils.getCurrentDateTimeUTC().toLocalDate()))
            throw new ValidationException("error.employee.age.too-young");
    }

    private void validatePassportNumber(EmployeeEntity entity) {
        boolean passportNumberInUse = employeeRepository.findUserIdByPassportNumber(entity.getPassportNumber())
                .map(userId -> !Objects.equals(userId, entity.getUserId()))
                .orElse(false);
        if (passportNumberInUse)
            throw new ValidationException("error.employee.passport-number.duplicate");
    }

    private void validateRestaurant(EmployeeEntity entity) {
        if (entity.getRestaurantId() == null || !restaurantRepository.existsById(entity.getRestaurantId()))
            throw new ValidationException("error.create-employee.restaurant.not-exists");
    }

    private void validateManager(EmployeeEntity entity) {
        EmployeeEntity manager;
        if (entity.getManagerUserId() == null || (manager = employeeRepository.findByUserId(entity.getManagerUserId()).orElse(null)) == null)
            throw new ValidationException("error.create-employee.manager.not-exists");
        if (manager.getPosition().getPriority() != entity.getPosition().getPriority() - 1)
            throw new ValidationException("error.create-employee.manager.invalid-position");
        if (manager.getPosition() != EmployeePositionEnum.TOP_MANAGER && !Objects.equals(manager.getRestaurantId(), entity.getRestaurantId()))
            throw new ValidationException("error.create-employee.manager.invalid-restaurant");
    }
}
