package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.MealMapper;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;
import ua.edu.ukma.db.kfc.repositories.MealIngredientRepository;
import ua.edu.ukma.db.kfc.rest.model.MealDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateMealDto;
import ua.edu.ukma.db.kfc.rest.model.MealIngredientDto;
import ua.edu.ukma.db.kfc.repositories.MealRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.MealValidator;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class MealService {

    @Inject
    private MealRepository repository;
    @Inject
    private MealIngredientRepository mealIngredientRepository;
    @Inject
    private IngredientRepository ingredientRepository;
    @Inject
    private MealValidator validator;
    @Inject
    private MealMapper mapper;


    public List<MealDto> getAllMeals() {
        List<MealEntity> meals = repository.findAll();

        for (MealEntity meal : meals) {
            List<MealIngredientEntity> ingredients = mealIngredientRepository.findByMealId(meal.getId());
            meal.setIngredients(ingredients);
        }

        validator.validForView(meals);

        return mapper.toResponse(meals);
    }

    public MealDto getMealById(int id) {
        MealEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meal with id " + id + " not found"));
        validator.validForView(entity);
        List<MealIngredientEntity> ingredients = mealIngredientRepository.findByMealId(entity.getId());
        entity.setIngredients(ingredients);
        return mapper.toResponse(entity);
    }

    public MealDto getMealByTitle(String title) {
        MealEntity entity = repository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException("Meal with title " + title + " not found"));
        validator.validForView(entity);
        List<MealIngredientEntity> ingredients = mealIngredientRepository.findByMealId(entity.getId());
        entity.setIngredients(ingredients);
        return mapper.toResponse(entity);
    }

    public int save(UpdateMealDto dto) {
        MealEntity meal = new MealEntity();
        mapper.toEntity(dto, meal);

        calculateEnergyWeightPriceSum(dto, meal);

        validator.validForCreate(meal);

        int mealId = repository.save(meal);

        dto.getIngredients().forEach(ingDto -> {
            MealIngredientEntity link = new MealIngredientEntity();
            link.setMealId(mealId);
            link.setIngredientId(ingDto.getIngredientId());
            link.setAmount(ingDto.getAmount());
            link.setFixated(ingDto.getIsFixated());
            mealIngredientRepository.save(link);
        });

        return mealId;
    }

    public int updateMeal(int id, UpdateMealDto dto) {
        MealEntity meal = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meal not found"));
        mapper.toEntity(dto, meal);
        validator.validForUpdate(meal);

        calculateEnergyWeightPriceSum(dto, meal);

        repository.delete(id);

        int mealId = repository.save(meal);

        dto.getIngredients().forEach(ingDto -> {
            MealIngredientEntity link = new MealIngredientEntity();
            link.setMealId(mealId);
            link.setIngredientId(ingDto.getIngredientId());
            link.setAmount(ingDto.getAmount());
            link.setFixated(ingDto.getIsFixated());
            mealIngredientRepository.save(link);
        });

        return mealId;
    }

    public void deleteMeal(int id) {
        MealEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meal not found"));
        validator.validForDelete(entity);
        repository.delete(id);
    }

    private void calculateEnergyWeightPriceSum(UpdateMealDto dto, MealEntity meal) {
        int totalEnergetic = 0;
        int totalWeight = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (MealIngredientDto ingDto : dto.getIngredients()) {
            IngredientEntity ingredient = ingredientRepository.findById(ingDto.getIngredientId())
                    .orElseThrow(() -> new NotFoundException("Ingredient not found"));
            totalEnergetic += ingredient.getEnergeticValue() * ingDto.getAmount();
            totalWeight += ingredient.getWeight() * ingDto.getAmount();
            totalPrice = totalPrice.add(ingredient.getPrice().multiply(BigDecimal.valueOf(ingDto.getAmount())));
        }
        meal.setEnergeticValue(totalEnergetic);
        meal.setWeight(totalWeight);
        meal.setPrice(totalPrice.add(BigDecimal.valueOf(dto.getAdditionalPrice())));
    }
}
