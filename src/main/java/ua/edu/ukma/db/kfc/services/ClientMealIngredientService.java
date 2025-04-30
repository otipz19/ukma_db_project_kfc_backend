package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.exceptions.ValidationException;
import ua.edu.ukma.db.kfc.mappers.ClientMealIngredientMapper;
import ua.edu.ukma.db.kfc.model.entities.*;
import ua.edu.ukma.db.kfc.repositories.*;
import ua.edu.ukma.db.kfc.validators.ClientMealIngredientValidator;
import ua.edu.ukma.db.kfc.rest.model.ClientMealIngredientDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClientMealIngredientService {

    @Inject
    private ClientMealIngredientValidator validator;
    @Inject
    private ClientMealIngredientRepository repository;
    @Inject
    private ClientMealIngredientMapper mapper;
    @Inject
    private MealIngredientService mealIngredientService;
    @Inject
    private MealRepository mealRepository;
    @Inject
    private IngredientRepository ingredientRepository;

    public List<ClientMealIngredientEntity> getByClientMealId(int clientMealId) {
        return repository.findUnionByClientMealIds(List.of(clientMealId));
    }

    public Map<Integer, List<ClientMealIngredientEntity>> getByClientMealIds(List<Integer> clientMealIds) {
        return repository.findUnionByClientMealIds(clientMealIds).stream()
                .collect(Collectors.groupingBy(ClientMealIngredientEntity::getClientMealId));
    }

    public void saveAll(int clientMealId, List<ClientMealIngredientDto> dtos) {
        List<ClientMealIngredientEntity> entities = mapper.toEntities(clientMealId, dtos);
        validator.validForCreate(entities);
        repository.saveAll(entities);
    }

    public DbCache fetchClientMealsIngredientsData(List<Integer> mealsIds) {
        Map<Integer, MealEntity> mealsMap = mealRepository.findByIds(mealsIds).stream()
                .collect(Collectors.toMap(MealEntity::getId, Function.identity()));
        if (mealsMap.size() != mealsIds.size())
            throw new ValidationException("error.create-order.meal.not-exists");
        Map<Integer, List<MealIngredientEntity>> baseMealIngredientsMap = mealIngredientService.getByMealIds(mealsIds);
        List<Integer> ingredientsIds = baseMealIngredientsMap.values().stream()
                .flatMap(List::stream)
                .map(MealIngredientEntity::getIngredientId)
                .distinct()
                .toList();
        Map<Integer, IngredientEntity> ingredientsMap = ingredientRepository.findByIds(ingredientsIds).stream()
                .collect(Collectors.toMap(IngredientEntity::getId, Function.identity()));

        return new DbCache(mealsMap, baseMealIngredientsMap, ingredientsMap);
    }

    public record DbCache(Map<Integer, MealEntity> mealsMap, Map<Integer, List<MealIngredientEntity>> baseMealIngredientsMap, Map<Integer, IngredientEntity> ingredientsMap) {}

    public IngredientsMergeResult merge(int mealId, List<ClientMealIngredientDto> overrides, DbCache dbCache) {
        Map<Integer, Integer> merged = new HashMap<>();
        List<ClientMealIngredientDto> overridden = new ArrayList<>();
        Map<Integer, ClientMealIngredientDto> overridesMap = overrides.stream()
                .collect(Collectors.toMap(ClientMealIngredientDto::getIngredientId, Function.identity()));

        for (MealIngredientEntity ingredient : dbCache.baseMealIngredientsMap().get(mealId)) {
            int ingredientId = ingredient.getIngredientId();
            int amount = ingredient.getAmount();
            ClientMealIngredientDto override = overridesMap.get(ingredientId);
            if (!ingredient.isFixated() && override != null && amount != override.getAmount()) {
                amount = override.getAmount();
                overridden.add(override);
            }
            merged.put(ingredientId, amount);
        }
        return new IngredientsMergeResult(merged, overridden);
    }

    public void calculateDerivedAttributes(ClientMealEntity clientMeal, Map<Integer, Integer> ingredientsAmounts, DbCache dbCache) {
        int energeticValue = 0;
        int weight = 0;
        BigDecimal price = dbCache.mealsMap().get(clientMeal.getMealId()).getAdditionalPrice();
        for (Map.Entry<Integer, Integer> mealIngredient : ingredientsAmounts.entrySet()) {
            IngredientEntity ingredient = dbCache.ingredientsMap().get(mealIngredient.getKey());
            int amount = mealIngredient.getValue();
            energeticValue += ingredient.getEnergeticValue() * amount;
            weight += ingredient.getWeight() * amount;
            price = price.add(ingredient.getPrice().multiply(BigDecimal.valueOf(amount)));
        }
        clientMeal.setEnergeticValue(energeticValue);
        clientMeal.setWeight(weight);
        clientMeal.setPrice(price);
    }

    public record IngredientsMergeResult(Map<Integer, Integer> merged, List<ClientMealIngredientDto> overridden) {}
}