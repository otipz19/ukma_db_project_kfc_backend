package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.ClientMealControllerApi;
import ua.edu.ukma.db.kfc.rest.model.ClientMealsFilterDto;
import ua.edu.ukma.db.kfc.services.ClientMealService;

@ApplicationScoped
public class ClientMealController implements ClientMealControllerApi {

    @Inject
    private ClientMealService clientMealService;

    @Override
    public Response getClientMealsByFilter(ClientMealsFilterDto filter) {
        return Response.ok(clientMealService.getClientMealsByFilter(filter)).build();
    }

    @Override
    public Response getClientMealById(Integer id) {
        return Response.ok(clientMealService.getClientMealById(id)).build();
    }
}
