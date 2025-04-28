package ua.edu.ukma.db.kfc.validators;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.security.SecurityContextHolder;

import java.util.List;
import java.util.Set;

public abstract class BaseValidator<E> implements IValidator<E> {

    @Inject
    protected Validator validator;
    @Inject
    protected SecurityContextHolder securityContextHolder;

    @Override
    public void validForView(E entity) {
        securityContextHolder.authorized();
    }

    @Override
    public void validForView(List<E> entities) {
        securityContextHolder.authorized();
    }

    @Override
    public void validForCreate(E entity) {
        securityContextHolder.authorized();
        validateData(entity);
    }

    @Override
    public void validForUpdate(E entity) {
        securityContextHolder.authorized();
        validateData(entity);
    }

    @Override
    public void validForDelete(E entity) {
        securityContextHolder.authorized();
    }

    protected void validateData(E entity) {
        Set<ConstraintViolation<E>> violations = validator.validate(entity);
        if (violations != null && !violations.isEmpty())
            throw new ValidationException(violations);
    }
}
