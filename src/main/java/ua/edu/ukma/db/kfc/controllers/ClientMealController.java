package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.ClientMealControllerApi;
import ua.edu.ukma.db.kfc.rest.model.ClientMealDto;
import ua.edu.ukma.db.kfc.rest.model.CreateClientMealDto;
import ua.edu.ukma.db.kfc.services.ClientMealService;

import java.util.List;

@ApplicationScoped
public class ClientMealController implements ClientMealControllerApi {

    @Inject
    private ClientMealService clientMealService;

    @Override
    public Response getAllClientMeals() {
        return Response.ok(clientMealService.getAllMeals()).build();
    }

    @Override
    public Response getClientMealById(Integer id) {
        return Response.ok(clientMealService.getClientMealById(id)).build();
    }

    @Override
    public Response createClientMeal(CreateClientMealDto dto) {
        return Response.ok(clientMealService.save(dto)).build();
    }

    @Override
    public Response deleteClientMeal(Integer id) {
        clientMealService.deleteMeal(id);
        return Response.noContent().build();
    }
}
