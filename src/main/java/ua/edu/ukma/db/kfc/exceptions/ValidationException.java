package ua.edu.ukma.db.kfc.exceptions;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;

@Getter
public class ValidationException extends RuntimeException {

    private final Set<? extends ConstraintViolation<?>> violations;

    public ValidationException(String message) {
        super(message);
        this.violations = null;
    }

    public <T> ValidationException(Set<ConstraintViolation<T>> violations) {
        super("Constraint violation");
        this.violations = violations;
    }
}
