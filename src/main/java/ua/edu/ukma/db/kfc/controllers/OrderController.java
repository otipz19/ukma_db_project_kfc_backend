package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.OrderControllerApi;
import ua.edu.ukma.db.kfc.rest.model.CreateOrderDto;
import ua.edu.ukma.db.kfc.services.OrderService;

@ApplicationScoped
public class OrderController implements OrderControllerApi {

    @Inject
    private OrderService orderService;

    @Override
    public Response getOrderById(Integer orderId) {
        return Response.ok(orderService.getOrderById(orderId)).build();
    }

    @Override
    public Response createOrder(CreateOrderDto createOrderDto) {
        return Response.ok(orderService.createOrder(createOrderDto)).build();
    }
}
