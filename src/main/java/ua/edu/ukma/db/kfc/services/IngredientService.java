package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.IngredientMapper;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.MealIngredientRepository;
import ua.edu.ukma.db.kfc.repositories.MealRepository;
import ua.edu.ukma.db.kfc.rest.model.IngredientDto;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;
import ua.edu.ukma.db.kfc.rest.model.UpdateIngredientDto;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.IngredientValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class IngredientService {

    @Inject
    private IngredientRepository repository;
    @Inject
    private IngredientValidator validator;
    @Inject
    private IngredientMapper mapper;
    @Inject
    private MealIngredientRepository mealIngredientRepository;
    @Inject
    private MealRepository mealRepository;


    public List<IngredientDto> getAllIngredients() {
        List<IngredientEntity> ingredients = repository.findAll();
        validator.validForView(ingredients);
        return mapper.toResponse(ingredients);
    }

    public IngredientDto getIngredientById(int id) {
        IngredientEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient with id " + id + " not found"));
        validator.validForView(entity);
        return mapper.toResponse(entity);
    }

    public IngredientDto getIngredientByTitle(String title) {
        IngredientEntity entity = repository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException("Ingredient with title " + title + " not found"));
        validator.validForView(entity);
        return mapper.toResponse(entity);
    }

    public int createIngredient(UpdateIngredientDto dto) {
        IngredientEntity entity = new IngredientEntity();
        mapper.toEntity(dto, entity);
        validator.validForCreate(entity);
        return repository.save(entity);
    }

    public int updateIngredient(int id, UpdateIngredientDto dto) {
        IngredientEntity ingredient = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
        mapper.toEntity(dto, ingredient);
        validator.validForUpdate(ingredient);

        repository.delete(id);
        int updatedIngredientId = repository.save(ingredient);

        List<MealIngredientEntity> affectedAssociations = mealIngredientRepository.findByIngredientId(id);
        if (!affectedAssociations.isEmpty()) {
            Map<Integer, List<MealIngredientEntity>> associationsByMeal = affectedAssociations.stream()
                    .collect(Collectors.groupingBy(MealIngredientEntity::getMealId));

            associationsByMeal.forEach((oldMealId, associations) -> {
                MealEntity meal = mealRepository.findById(oldMealId)
                        .orElseThrow(() -> new NotFoundException("Meal with id " + oldMealId + " not found"));

                recalculateMealAggregates(meal);

                mealRepository.delete(oldMealId);
                int newMealId = mealRepository.save(meal);


                associations.forEach(assoc -> {
                    MealIngredientEntity newAssoc = new MealIngredientEntity();
                    newAssoc.setMealId(newMealId);
                    newAssoc.setIngredientId(updatedIngredientId);
                    newAssoc.setAmount(assoc.getAmount());
                    newAssoc.setFixated(assoc.isFixated());

                    mealIngredientRepository.save(newAssoc);
                });
            });
        }

        return updatedIngredientId;
    }

    private void recalculateMealAggregates(MealEntity meal) {
        List<MealIngredientEntity> associations = mealIngredientRepository.findByMealId(meal.getId());
        int totalEnergetic = 0;
        int totalWeight = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (MealIngredientEntity assoc : associations) {
            IngredientEntity ing = repository.findById(assoc.getIngredientId())
                    .orElseThrow(() -> new NotFoundException("Ingredient not found"));
            totalEnergetic += ing.getEnergeticValue() * assoc.getAmount();
            totalWeight += ing.getWeight() * assoc.getAmount();
            totalPrice = totalPrice.add(ing.getPrice().multiply(BigDecimal.valueOf(assoc.getAmount())));
        }
        meal.setEnergeticValue(totalEnergetic);
        meal.setWeight(totalWeight);
        meal.setPrice(totalPrice.add(meal.getAdditionalPrice()));
    }

    public void deleteIngredient(int id) {
        IngredientEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
        validator.validForDelete(entity);
        repository.delete(id);
    }
}