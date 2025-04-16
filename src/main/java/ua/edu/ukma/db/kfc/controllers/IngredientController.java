package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.services.IngredientService;
import ua.edu.ukma.db.kfc.rest.api.IngredientControllerApi;
import ua.edu.ukma.db.kfc.rest.model.UpdateIngredientDto;

import java.util.List;

@ApplicationScoped
public class IngredientController implements IngredientControllerApi {

    @Inject
    private IngredientService ingredientService;

    @Override
    public Response getAllIngredients(List<Integer> ids) {
        return Response.ok(ingredientService.getAllIngredients(ids)).build();
    }

    @Override
    public Response getIngredientById(Integer id) {
        return Response.ok(ingredientService.getIngredientById(id)).build();
    }

    @Override
    public Response getIngredientByTitle(String title) {
        return Response.ok(ingredientService.getIngredientByTitle(title)).build();
    }

    @Override
    public Response createIngredient(UpdateIngredientDto dto) {
        return Response.ok(ingredientService.createIngredient(dto)).build();
    }

    @Override
    public Response updateIngredient(Integer ingredientId, UpdateIngredientDto updateIngredientDto) {
        return Response.ok(ingredientService.updateIngredient(ingredientId, updateIngredientDto)).build();
    }

    @Override
    public Response deleteIngredient(Integer id) {
        ingredientService.deleteIngredient(id);
        return Response.noContent().build();
    }
}