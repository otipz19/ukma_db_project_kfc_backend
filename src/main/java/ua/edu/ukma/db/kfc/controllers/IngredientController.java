package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.services.IngredientService;
import ua.edu.ukma.db.kfc.rest.api.IngredientControllerApi;
import ua.edu.ukma.db.kfc.rest.model.IngredientUpsertDto;

@ApplicationScoped
public class IngredientController implements IngredientControllerApi {

    @Inject
    private IngredientService ingredientService;

    @Override
    public Response getAllIngredients() {
        return Response.ok(ingredientService.getAllIngredients()).build();
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
    public Response updateIngredient(Integer ingredientId, IngredientUpsertDto updateIngredientDto) {
        ingredientService.updateIngredient(ingredientId, updateIngredientDto);
        return Response.noContent().build();
    }

    @Override
    public Response createIngredient(IngredientUpsertDto dto) {
        ingredientService.createIngredient(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    public Response deleteIngredient(Integer id) {
        ingredientService.deleteIngredient(id);
        return Response.noContent().build();
    }
}