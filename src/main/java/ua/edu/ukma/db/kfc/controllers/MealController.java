package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.MealControllerApi;
import ua.edu.ukma.db.kfc.rest.model.UpdateMealDto;
import ua.edu.ukma.db.kfc.services.MealService;

@ApplicationScoped
public class MealController implements MealControllerApi {

    @Inject
    private MealService mealService;

    @Override
    public Response getAllMeals() {
        return Response.ok(mealService.getAllMeals()).build();
    }

    @Override
    public Response getMealById(Integer id) {
        return Response.ok(mealService.getMealById(id)).build();
    }

    @Override
    public Response getMealByTitle(String title) {
        return Response.ok(mealService.getMealByTitle(title)).build();
    }

    @Override
    public Response createMeal(UpdateMealDto dto) {
        return Response.ok(mealService.saveMeal(dto)).build();
    }

    @Override
    public Response updateMeal(Integer mealId, UpdateMealDto dto) {
        return Response.ok(mealService.updateMeal(mealId, dto)).build();
    }

    @Override
    public Response deleteMeal(Integer id) {
        mealService.deleteMeal(id);
        return Response.noContent().build();
    }
}