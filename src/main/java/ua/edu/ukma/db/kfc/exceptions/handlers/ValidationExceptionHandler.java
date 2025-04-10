package ua.edu.ukma.db.kfc.exceptions.handlers;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.mappers.messages.MessageSource;

import java.util.List;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {

    @Inject
    private MessageSource messageSource;

    @Override
    public Response toResponse(ValidationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(getMessages(exception))
                .build();
    }

    private List<String> getMessages(ValidationException exception) {
        if (exception.getViolations() == null)
            return List.of(messageSource.getMessage(exception.getMessage()));
        return exception.getViolations().stream()
                .map(ConstraintViolation::getMessage)
                .map(messageSource::getMessage)
                .toList();
    }
}
