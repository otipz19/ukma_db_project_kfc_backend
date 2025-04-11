package ua.edu.ukma.db.kfc.services;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.MealIngredientRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.MealIngredientValidator;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class MealIngredientService {

    @Inject
    private MealIngredientValidator validator;
    @Inject
    private MealIngredientRepository repository;


    public List<MealIngredientEntity> getAllMealIngredients() {
        List<MealIngredientEntity> mealIngredients = repository.findAll();
        validator.validForView(mealIngredients);
        return mealIngredients;
    }

    public MealIngredientEntity getMealIngredient(int mealId, int ingredientId) {
        Optional<MealIngredientEntity> entityOpt = repository.findByMealIdAndIngredientId(mealId, ingredientId);
        return entityOpt.orElseThrow(() -> new NotFoundException(
                "Meal-Ingredient association not found for mealId " + mealId + " and ingredientId " + ingredientId));
    }


    public String createMealIngredient(MealIngredientEntity dto) {
        validator.validForCreate(dto);
        return repository.save(dto);
    }

    public String updateMealIngredient(MealIngredientEntity dto) {
        int mealId = dto.getMealId();
        int ingredientId = dto.getIngredientId();
        MealIngredientEntity existing = repository.findByMealIdAndIngredientId(mealId, ingredientId)
                .orElseThrow(() -> new NotFoundException(
                        "Meal-Ingredient association not found for mealId " + mealId + " and ingredientId " + ingredientId));

        existing.setAmount(dto.getAmount());
        existing.setFixated(dto.isFixated());
        validator.validForUpdate(existing);

        repository.update(existing);
        return mealId + "-" + ingredientId;
    }

    public void deleteMealIngredient(int mealId, int ingredientId) {
        repository.findByMealIdAndIngredientId(mealId, ingredientId)
                .orElseThrow(() -> new NotFoundException(
                        "Meal-Ingredient association not found for mealId " + mealId + " and ingredientId " + ingredientId));

        repository.delete(mealId, ingredientId);
    }
}
