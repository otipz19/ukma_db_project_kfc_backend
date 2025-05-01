package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.StatisticControllerApi;
import ua.edu.ukma.db.kfc.services.StatisticsService;

@ApplicationScoped
public class StatisticsController implements StatisticControllerApi {

    @Inject
    private StatisticsService service;

    @Override
    public Response getMealPopularity() {
        return Response.ok(service.getMealPopular()).build();
    }

    @Override
    public Response getMealsNotOrderedByBonusClients() {
        return Response.ok(service.getMealsNotOrderedByBonusClients()).build();
    }

    @Override
    public Response getMealLeastPopular() {
        return Response.ok(service.getMealLeastPopular()).build();
    }

    @Override
    public Response getTopManagersLastQuarter() {
        return Response.ok(service.getTopManagersLastQuarter()).build();
    }

    @Override
    public Response getCashiersAlwaysHighEnergy(Integer threshold) {
        return Response.ok(service.getCashiersAlwaysHighEnergy(threshold)).build();
    }

    @Override
    public Response getClientMealAveragesByRestaurant(Integer clientId) {
        return Response.ok(service.getClientMealAveragesByRestaurant(clientId)).build();
    }

    @Override
    public Response getEmployeeMealAveragesByRestaurant(Integer restaurantId) {
        return Response.ok(service.getEmployeeMealAveragesByRestaurant(restaurantId)).build();
    }

    @Override
    public Response getIngredientsOnlyInOrderedMeals() {
        return Response.ok(service.getIngredientsOnlyInOrderedMeals()).build();
    }
}
