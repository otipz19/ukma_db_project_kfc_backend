package ua.edu.ukma.db.kfc.services;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.ClientMealMapper;
import ua.edu.ukma.db.kfc.model.entities.ClientMealEntity;
import ua.edu.ukma.db.kfc.model.entities.ClientMealIngredientEntity;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.ClientMealRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.ClientMealValidator;
import ua.edu.ukma.db.kfc.rest.model.ClientMealDto;
import ua.edu.ukma.db.kfc.rest.model.ClientMealIngredientDto;
import ua.edu.ukma.db.kfc.rest.model.CreateClientMealDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class ClientMealService {
    @Inject
    private ClientMealRepository repository;
    @Inject
    private ClientMealValidator validator;
    @Inject
    private ClientMealMapper mapper;
    @Inject
    private ClientMealIngredientService clientMealIngredientService;
    @Inject
    private MealIngredientService mealIngredientService;

    public List<ClientMealDto> getAllMeals() {
        List<ClientMealEntity> meals = repository.findAll();
        validator.validForView(meals);
        Map<Integer, List<ClientMealIngredientEntity>> ingredientsMap = clientMealIngredientService.getByClientMealIds(
                meals.stream().map(ClientMealEntity::getId).toList()
        );
        return mapper.toResponse(meals, ingredientsMap);
    }

    public ClientMealDto getMealById(int id) {
        ClientMealEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        List<ClientMealIngredientEntity> ingredients = clientMealIngredientService.getByClientMealId(entity.getId());
        return mapper.toResponse(entity, ingredients);
    }

    public int save(CreateClientMealDto dto) {
        ClientMealEntity meal = new ClientMealEntity();
        mapper.toEntity(dto, meal);

        List<MealIngredientEntity> baseEntities =
                mealIngredientService.getByMealId(dto.getMealId());
        Map<Integer, Integer> baseAmounts = baseEntities.stream()
                .collect(Collectors.toMap(
                        MealIngredientEntity::getIngredientId,
                        MealIngredientEntity::getAmount
                ));

        Map<Integer, Integer> customAmounts = dto.getIngredients().stream()
                .collect(Collectors.toMap(
                        ClientMealIngredientDto::getIngredientId,
                        ClientMealIngredientDto::getAmount
                ));

        if (baseAmounts.equals(customAmounts)) {

        }
        else {
            clientMealIngredientService.calculateDerivedAttributes(meal, dto.getIngredients());
        }

        validator.validForCreate(meal);

        int mealId = repository.save(meal);
        if (!customAmounts.isEmpty()) {
            clientMealIngredientService.saveAll(mealId, dto.getIngredients());
        }

        return mealId;
    }

    public void deleteMeal(int id) {
        ClientMealEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForDelete(entity);
        repository.delete(id);
    }
}
