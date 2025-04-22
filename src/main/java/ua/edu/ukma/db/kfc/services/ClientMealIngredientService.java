package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.mappers.ClientMealIngredientMapper;
import ua.edu.ukma.db.kfc.model.entities.*;
import ua.edu.ukma.db.kfc.repositories.*;
import ua.edu.ukma.db.kfc.validators.ClientMealIngredientValidator;
import ua.edu.ukma.db.kfc.rest.model.ClientMealIngredientDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    private IngredientRepository ingredientRepository;
    @Inject
    private MealRepository mealRepository;

    public List<ClientMealIngredientEntity> getByClientMealId(int mealId) {
        return repository.findByClientMealId(mealId);
    }

    public Map<Integer, List<ClientMealIngredientEntity>> getByClientMealIds(List<Integer> mealIds) {
        return repository.findByClientMealIds(mealIds).stream()
                .collect(Collectors.groupingBy(ClientMealIngredientEntity::getClientMealId));
    }

    public void saveAll(int mealId, List<ClientMealIngredientDto> dtos) {
        List<ClientMealIngredientEntity> entities = mapper.toEntities(mealId, dtos);
        validator.validForCreate(entities);
        repository.saveAll(entities);
    }

    public void calculateDerivedAttributes(ClientMealEntity clientMeal, List<ClientMealIngredientDto> ingredients) {
        Map<Integer, Integer> amounts = ingredients.stream().collect(Collectors.toMap(ClientMealIngredientDto::getIngredientId, ClientMealIngredientDto::getAmount));
        int energeticValue = 0;
        int weight = 0;
        BigDecimal price = BigDecimal.ZERO;
        for (IngredientEntity ingredient : ingredientRepository.findByIds(amounts.keySet())) {
            int amount = amounts.getOrDefault(ingredient.getId(), 0);
            energeticValue += ingredient.getEnergeticValue() * amount;
            weight += ingredient.getWeight() * amount;
            price = price.add(ingredient.getPrice().multiply(BigDecimal.valueOf(amount)));
        }
        MealEntity meal = mealRepository.findById(clientMeal.getMealId()).orElse(null);
        clientMeal.setEnergeticValue(energeticValue);
        clientMeal.setWeight(weight);
        clientMeal.setPrice(price.add(meal.getAdditionalPrice()));
    }

}