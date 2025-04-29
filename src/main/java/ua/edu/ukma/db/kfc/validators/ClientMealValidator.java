package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.model.entities.ClientMealEntity;
import ua.edu.ukma.db.kfc.repositories.OrderRepository;

import java.util.List;

@ApplicationScoped
public class ClientMealValidator extends BaseValidator<ClientMealEntity>{

    @Inject
    private OrderValidator orderValidator;
    @Inject
    private OrderRepository orderRepository;

    @Override
    public void validForView(ClientMealEntity entity) {
        orderValidator.validForView(orderRepository.findById(entity.getOrderId()).orElseThrow());
    }

    @Override
    public void validForView(List<ClientMealEntity> entities) {
        List<Integer> orderIds = entities.stream()
                .map(ClientMealEntity::getOrderId)
                .toList();
        orderValidator.validForView(orderRepository.findByIds(orderIds));
    }

    @Override
    public void validForCreate(ClientMealEntity entity) {
        throw new ForbiddenException(); // only batch creation is allowed
    }

    public void validForCreate(List<ClientMealEntity> entities) {
        if (entities.isEmpty())
            throw new ValidationException("error.create-order.no-client-meals");
        entities.forEach(this::validateData);
    }

    @Override
    public void validForUpdate(ClientMealEntity entity) {
        throw new ForbiddenException();
    }

    @Override
    public void validForDelete(ClientMealEntity entity) {
        throw new ForbiddenException();
    }

}
