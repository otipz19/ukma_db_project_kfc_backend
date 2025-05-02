package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.model.CreateIngredientDto;
import ua.edu.ukma.db.kfc.rest.model.IngredientsFilterDto;
import ua.edu.ukma.db.kfc.rest.model.ValuableIngredientsFilterDto;
import ua.edu.ukma.db.kfc.services.IngredientService;
import ua.edu.ukma.db.kfc.rest.api.IngredientControllerApi;
import ua.edu.ukma.db.kfc.rest.model.UpdateIngredientDto;

@ApplicationScoped
public class IngredientController implements IngredientControllerApi {

    @Inject
    private IngredientService ingredientService;

    @Override
    public Response getIngredientsByFilter(IngredientsFilterDto filter) {
        return Response.ok(ingredientService.getIngredientsByFilter(filter)).build();
    }

    @Override
    public Response getValuableIngredients(ValuableIngredientsFilterDto filter) {
        return Response.ok(ingredientService.getValuableIngredients(filter)).build();
    }

    @Override
    public Response getIngredientById(Integer id, Boolean requireActual) {
        return Response.ok(ingredientService.getIngredientById(id, requireActual)).build();
    }

    @Override
    public Response createIngredient(CreateIngredientDto dto) {
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