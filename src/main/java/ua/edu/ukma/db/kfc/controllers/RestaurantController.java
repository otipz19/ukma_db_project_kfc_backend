package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.RestaurantControllerApi;
import ua.edu.ukma.db.kfc.rest.model.RestaurantsFilterDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateRestaurantDto;
import ua.edu.ukma.db.kfc.services.RestaurantService;

@ApplicationScoped
public class RestaurantController implements RestaurantControllerApi {

    @Inject
    private RestaurantService service;

    @Override
    public Response getRestaurantsByFilter(RestaurantsFilterDto filter) {
        return Response.ok(service.getRestaurantsByFilter(filter)).build();
    }

    @Override
    public Response getRestaurantById(Integer restaurantId) {
        return Response.ok(service.getRestaurantById(restaurantId)).build();
    }

    @Override
    public Response createRestaurant(UpdateRestaurantDto updateRestaurantDto) {
        return Response.ok(service.createRestaurant(updateRestaurantDto)).build();
    }

    @Override
    public Response updateRestaurantById(Integer restaurantId, UpdateRestaurantDto updateRestaurantDto) {
        service.updateRestaurantById(restaurantId, updateRestaurantDto);
        return Response.noContent().build();
    }

    @Override
    public Response deleteRestaurantById(Integer restaurantId) {
        service.deleteRestaurantById(restaurantId);
        return Response.noContent().build();
    }
}
