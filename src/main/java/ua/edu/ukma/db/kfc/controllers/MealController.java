package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.MealControllerApi;
import ua.edu.ukma.db.kfc.rest.model.MealsFilterDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateMealDto;
import ua.edu.ukma.db.kfc.services.MealService;

@ApplicationScoped
public class MealController implements MealControllerApi {

    @Inject
    private MealService mealService;

    @Override
    public Response getMealsByFilter(MealsFilterDto filter) {
        return Response.ok(mealService.getMealsByFilter(filter)).build();
    }

    @Override
    public Response getMealById(Integer id, Boolean requireActual) {
        return Response.ok(mealService.getMealById(id, requireActual)).build();
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