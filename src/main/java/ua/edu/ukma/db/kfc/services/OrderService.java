package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.filters.OrdersFilter;
import ua.edu.ukma.db.kfc.mappers.OrderMapper;
import ua.edu.ukma.db.kfc.model.entities.OrderEntity;
import ua.edu.ukma.db.kfc.repositories.OrderRepository;
import ua.edu.ukma.db.kfc.rest.model.CreateOrderDto;
import ua.edu.ukma.db.kfc.rest.model.OrderDto;
import ua.edu.ukma.db.kfc.rest.model.OrdersFilterDto;
import ua.edu.ukma.db.kfc.rest.model.OrdersListDto;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.utils.TimeUtils;
import ua.edu.ukma.db.kfc.validators.OrderValidator;

import java.util.List;

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
    private ClientMealService clientMealService;

    public OrderDto getOrderById(int orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(NotFoundException::new);
        orderValidator.validForView(orderEntity);
        return orderMapper.toResponse(orderEntity);
    }

    public OrdersListDto getOrdersByFilter(OrdersFilterDto filterDto) {
        OrdersFilter filter = new OrdersFilter(filterDto);
        List<OrderEntity> orders = orderRepository.findByFilter(filter);
        orderValidator.validForView(orders);
        long total = orderRepository.countByFilter(filter);
        return orderMapper.toResponse(orders, total);
    }

    public int createOrder(CreateOrderDto createOrderDto) {
        OrderEntity orderEntity = new OrderEntity();
        orderMapper.toEntity(createOrderDto, orderEntity);
        orderEntity.setDateCreated(TimeUtils.getCurrentDateTimeUTC());
        orderEntity.setClientId(clientService.getIdByUserId(createOrderDto.getClientUserId()));
        orderValidator.validForCreate(orderEntity);
        int id = orderRepository.save(orderEntity);

        clientMealService.createClientMeals(id, createOrderDto.getClientMeals());

        orderRepository.addOrderBonuses(id, orderEntity.getClientId());

        return id;
    }

    public void completeOrder(int orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(NotFoundException::new);
        orderValidator.validForComplete(orderEntity);
        orderRepository.complete(orderId);
    }
}
