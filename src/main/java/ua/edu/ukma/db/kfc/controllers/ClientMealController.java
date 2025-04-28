package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.ClientMealControllerApi;
import ua.edu.ukma.db.kfc.services.ClientMealService;

@ApplicationScoped
public class ClientMealController implements ClientMealControllerApi {

    @Inject
    private ClientMealService clientMealService;

    @Override
    public Response getAllClientMeals(Integer orderId) {
        return Response.ok(clientMealService.getAllClientMeals(orderId)).build();
    }

    @Override
    public Response getClientMealById(Integer id) {
        return Response.ok(clientMealService.getClientMealById(id)).build();
    }
}
