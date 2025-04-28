package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.mappers.MealIngredientMapper;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;
import ua.edu.ukma.db.kfc.repositories.MealIngredientRepository;
import ua.edu.ukma.db.kfc.repositories.MealRepository;
import ua.edu.ukma.db.kfc.rest.model.MealIngredientDto;
import ua.edu.ukma.db.kfc.validators.MealIngredientValidator;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class MealIngredientService {

    @Inject
    private MealIngredientValidator validator;
    @Inject
    private MealIngredientRepository repository;
    @Inject
    private MealIngredientMapper mapper;
    @Inject
    private IngredientRepository ingredientRepository;
    @Inject
    private MealRepository mealRepository;

    public List<MealIngredientEntity> getByMealId(int mealId) {
        return repository.findByMealId(mealId);
    }

    public Map<Integer, List<MealIngredientEntity>> getByMealIds(List<Integer> mealIds) {
        return repository.findByMealIds(mealIds).stream()
                .collect(Collectors.groupingBy(MealIngredientEntity::getMealId));
    }

    public void saveAll(int mealId, List<MealIngredientDto> dtos) {
        List<MealIngredientEntity> entities = mapper.toEntities(mealId, dtos);
        validator.validForCreate(entities);
        repository.saveAll(entities);
    }

    public void updateMealsContainingIngredient(IngredientEntity ingredient, IngredientEntity oldIngredient) {
        List<MealEntity> affectedMeals = mealRepository.findByIngredientId(oldIngredient.getId());
        if (affectedMeals.isEmpty()) return;
        Map<Integer, List<MealIngredientEntity>> affectedAssociationsMap = getByMealIds(affectedMeals.stream().map(MealEntity::getId).toList());
        for (MealEntity meal : affectedMeals)
            updateAffectedEntities(meal, affectedAssociationsMap.get(meal.getId()), oldIngredient, ingredient);
        mealRepository.deleteAll(affectedAssociationsMap.keySet());
        List<Integer> newIds = mealRepository.saveAll(affectedMeals);
        for (int i = 0; i < affectedMeals.size(); i++) {
            int oldId = affectedMeals.get(i).getId();
            int newId = newIds.get(i);
            affectedAssociationsMap.get(oldId).forEach(a -> a.setMealId(newId));
        }
        repository.saveAll(affectedAssociationsMap.values().stream().flatMap(List::stream).toList());
    }

    private void updateAffectedEntities(MealEntity meal, List<MealIngredientEntity> associations, IngredientEntity oldIngredient, IngredientEntity newIngredient) {
        for (MealIngredientEntity association : associations) {
            if (!Objects.equals(association.getIngredientId(), oldIngredient.getId())) continue;
            int amount = association.getAmount();
            int energeticValue = meal.getEnergeticValue() + (newIngredient.getEnergeticValue() - oldIngredient.getEnergeticValue()) * amount;
            int weight = meal.getWeight() + (newIngredient.getWeight() - oldIngredient.getWeight()) * amount;
            BigDecimal price = meal.getPrice().add(newIngredient.getPrice().subtract(oldIngredient.getPrice()).multiply(BigDecimal.valueOf(amount)));
            meal.setEnergeticValue(energeticValue);
            meal.setWeight(weight);
            meal.setPrice(price);
            association.setIngredientId(newIngredient.getId());
            return;
        }
    }

    public void calculateDerivedAttributes(MealEntity meal, List<MealIngredientDto> ingredients) {
        Map<Integer, Integer> amounts = ingredients.stream().collect(Collectors.toMap(MealIngredientDto::getIngredientId, MealIngredientDto::getAmount));
        int energeticValue = 0;
        int weight = 0;
        BigDecimal price = BigDecimal.ZERO;
        for (IngredientEntity ingredient : ingredientRepository.findByIds(amounts.keySet())) {
            int amount = amounts.getOrDefault(ingredient.getId(), 0);
            energeticValue += ingredient.getEnergeticValue() * amount;
            weight += ingredient.getWeight() * amount;
            price = price.add(ingredient.getPrice().multiply(BigDecimal.valueOf(amount)));
        }
        meal.setEnergeticValue(energeticValue);
        meal.setWeight(weight);
        meal.setPrice(price.add(meal.getAdditionalPrice()));
    }
}
