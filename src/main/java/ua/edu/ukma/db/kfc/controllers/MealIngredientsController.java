package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.MealIngredientControllerApi;
import ua.edu.ukma.db.kfc.rest.model.UpdateMealIngredientDto;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.services.MealIngredientService;

import java.util.List;

@ApplicationScoped
public class MealIngredientsController implements MealIngredientControllerApi {

    @Inject
    private MealIngredientService mealIngredientService;

    private MealIngredientEntity toEntity(UpdateMealIngredientDto dto) {
        MealIngredientEntity entity = new MealIngredientEntity();
        entity.setMealId(dto.getMealId());
        entity.setIngredientId(dto.getIngredientId());
        entity.setAmount(dto.getAmount());
        entity.setFixated(dto.getIsFixated());
        return entity;
    }

    @Override
    public Response createMealIngredient(UpdateMealIngredientDto updateMealIngredientDto) {
        MealIngredientEntity entity = toEntity(updateMealIngredientDto);
        String compositeKey = mealIngredientService.createMealIngredient(entity);
        return Response.ok(compositeKey).build();
    }

    @Override
    public Response deleteMealIngredient(Integer mealId, Integer ingredientId) {
        mealIngredientService.deleteMealIngredient(mealId, ingredientId);
        return Response.noContent().build();
    }

    @Override
    public Response getAllMealIngredients() {
        List<MealIngredientEntity> mealIngredients = mealIngredientService.getAllMealIngredients();
        return Response.ok(mealIngredients).build();
    }

    @Override
    public Response getMealIngredient(Integer mealId, Integer ingredientId) {
        MealIngredientEntity entity = mealIngredientService.getMealIngredient(mealId, ingredientId);
        return Response.ok(entity).build();
    }

    @Override
    public Response updateMealIngredient(Integer mealId, Integer ingredientId, UpdateMealIngredientDto updateMealIngredientDto) {
        updateMealIngredientDto.setMealId(mealId);
        updateMealIngredientDto.setIngredientId(ingredientId);
        MealIngredientEntity entity = toEntity(updateMealIngredientDto);
        String compositeKey = mealIngredientService.updateMealIngredient(entity);
        return Response.ok(compositeKey).build();
    }
}
