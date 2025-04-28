package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.OrderMapper;
import ua.edu.ukma.db.kfc.model.entities.OrderEntity;
import ua.edu.ukma.db.kfc.repositories.OrderRepository;
import ua.edu.ukma.db.kfc.rest.model.CreateOrderDto;
import ua.edu.ukma.db.kfc.rest.model.OrderDto;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.utils.TimeUtils;
import ua.edu.ukma.db.kfc.validators.OrderValidator;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class OrderService {

    @Inject
    private OrderRepository orderRepository;
    @Inject
    private OrderMapper orderMapper;
    @Inject
    private OrderValidator orderValidator;
    @Inject
    private ClientService clientService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private ClientMealService clientMealService;

    public OrderDto getOrderById(int orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(NotFoundException::new);
        orderValidator.validForView(orderEntity);
        return orderMapper.toResponse(orderEntity);
    }

    public int createOrder(CreateOrderDto createOrderDto) {
        OrderEntity orderEntity = new OrderEntity();
        orderMapper.toEntity(createOrderDto, orderEntity);
        orderEntity.setDateCreated(TimeUtils.getCurrentDateTimeUTC());
        orderEntity.setClientId(clientService.getIdByUserId(createOrderDto.getClientUserId()));
        orderEntity.setEmployeeId(employeeService.getIdByUserId(createOrderDto.getEmployeeUserId()));
        orderValidator.validForCreate(orderEntity);
        int id = orderRepository.save(orderEntity);

        clientMealService.createClientMeals(id, createOrderDto.getClientMeals());

        return id;
    }

    public void completeOrder(int orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(NotFoundException::new);
        orderValidator.validForComplete(orderEntity);
        orderRepository.complete(orderId);
    }
}
