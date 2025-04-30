package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.filters.MealsFilter;
import ua.edu.ukma.db.kfc.mappers.MealMapper;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.MealDto;
import ua.edu.ukma.db.kfc.rest.model.MealsFilterDto;
import ua.edu.ukma.db.kfc.rest.model.MealsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateMealDto;
import ua.edu.ukma.db.kfc.repositories.MealRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.MealValidator;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class MealService {

    @Inject
    private MealRepository repository;
    @Inject
    private MealValidator validator;
    @Inject
    private MealMapper mapper;
    @Inject
    private MealIngredientService mealIngredientService;

    public MealsListDto getMealsByFilter(MealsFilterDto filterDto) {
        MealsFilter filter = new MealsFilter(filterDto);
        List<MealEntity> meals = repository.findByFilter(filter);
        validator.validForView(meals);
        Map<Integer, List<MealIngredientEntity>> ingredientsMap = mealIngredientService.getByMealIds(
                meals.stream().map(MealEntity::getId).toList()
        );
        long total = repository.countByFilter(filter);
        return mapper.toResponse(meals, ingredientsMap, total);
    }

    public MealDto getMealById(int id, boolean requireActual) {
        MealEntity entity = repository.findById(id, requireActual).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        List<MealIngredientEntity> ingredients = mealIngredientService.getByMealId(entity.getId());
        return mapper.toResponse(entity, ingredients);
    }

    public int saveMeal(UpdateMealDto dto) {
        MealEntity meal = new MealEntity();
        mapper.toEntity(dto, meal);
        mealIngredientService.calculateDerivedAttributes(meal, dto.getIngredients());

        validator.validForCreate(meal);

        int mealId = repository.save(meal);
        mealIngredientService.saveAll(mealId, dto.getIngredients());

        return mealId;
    }

    public int updateMeal(int id, UpdateMealDto dto) {
        MealEntity meal = repository.findById(id).orElseThrow(NotFoundException::new);
        mapper.toEntity(dto, meal);
        mealIngredientService.calculateDerivedAttributes(meal, dto.getIngredients());

        validator.validForUpdate(meal);

        repository.delete(id);

        int mealId = repository.save(meal);
        mealIngredientService.saveAll(mealId, dto.getIngredients());

        return mealId;
    }

    public void deleteMeal(int id) {
        MealEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForDelete(entity);
        repository.delete(id);
    }
}
