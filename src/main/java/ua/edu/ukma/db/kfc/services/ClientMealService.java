package ua.edu.ukma.db.kfc.services;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.ClientMealMapper;
import ua.edu.ukma.db.kfc.model.entities.ClientMealEntity;
import ua.edu.ukma.db.kfc.model.entities.ClientMealIngredientEntity;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.repositories.ClientMealRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.ClientMealValidator;
import ua.edu.ukma.db.kfc.rest.model.ClientMealDto;
import ua.edu.ukma.db.kfc.rest.model.MealDto;
import ua.edu.ukma.db.kfc.rest.model.ClientMealIngredientDto;
import ua.edu.ukma.db.kfc.rest.model.MealIngredientDto;
import ua.edu.ukma.db.kfc.rest.model.CreateClientMealDto;

import java.math.BigDecimal;
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
    private MealService mealService;
    @Inject
    private IngredientService ingredientService;
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

    public ClientMealDto getClientMealById(int id) {
        ClientMealEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        List<ClientMealIngredientEntity> ingredients = clientMealIngredientService.getByClientMealId(entity.getId());
        return mapper.toResponse(entity, ingredients);
    }

    public List<Integer> getClientMealsIdsByOrderId(int orderId) {
        List<ClientMealEntity> meals = repository.findByOrderId(orderId);
        validator.validForView(meals);
        if (meals.isEmpty()) {
            return List.of();
        }
        return meals.stream()
                .map(ClientMealEntity::getId)
                .collect(Collectors.toList());
    }

    public int save(CreateClientMealDto dto) {
        ClientMealEntity meal = new ClientMealEntity();
        mapper.toEntity(dto, meal);

        if (dto.getIngredients().isEmpty()) {
            meal.setPrice(BigDecimal.ZERO);
        }
        else {
            clientMealIngredientService.calculateDerivedAttributes(meal, dto.getIngredients());
        }

        validator.validForCreate(meal);

        int mealId = repository.save(meal);
        if (!dto.getIngredients().isEmpty()) {
            clientMealIngredientService.saveAll(mealId, dto.getIngredients());
        }

        return mealId;
    }

    public void deleteMeal(int id) {
        ClientMealEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForDelete(entity);
        repository.delete(id);
    }

    public BigDecimal calculateCost(@NotNull List<CreateClientMealDto> clientMeals) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (CreateClientMealDto mealData : clientMeals) {
            MealDto meal = mealService.getMealById(mealData.getMealId());

            BigDecimal mealCost;
            List<ClientMealIngredientDto> customIngredients = mealData.getIngredients();

            if (customIngredients != null && !customIngredients.isEmpty()) {
                mealCost = meal.getAdditionalPrice();

                for (ClientMealIngredientDto ingDto : customIngredients) {
                    BigDecimal unitPrice = ingredientService
                            .getIngredientById(ingDto.getIngredientId())
                            .getPrice();
                    BigDecimal amount = BigDecimal.valueOf(ingDto.getAmount());
                    mealCost = mealCost.add(unitPrice.multiply(amount));
                }
            } else {
                mealCost = meal.getPrice();
            }

            BigDecimal count = BigDecimal.valueOf(mealData.getAmountInOrder());
            mealCost = mealCost.multiply(count);

            totalCost = totalCost.add(mealCost);
        }

        return totalCost;
    }

    public void createClientMeals(int orderId, @NotNull List<CreateClientMealDto> clientMeals) {
        for (CreateClientMealDto mealData : clientMeals) {
            Integer mealId = mealData.getMealId();

            List<ClientMealIngredientDto> ingredientsData = mealData.getIngredients();

            List<ClientMealIngredientDto> ingredientDtos = null;
            if (ingredientsData != null && !ingredientsData.isEmpty()) {
                ingredientDtos = ingredientsData.stream()
                        .map(data -> {
                            ClientMealIngredientDto dto = new ClientMealIngredientDto();
                            dto.setIngredientId(data.getIngredientId());
                            dto.setAmount(data.getAmount());
                            return dto;
                        })
                        .toList();
            }

            CreateClientMealDto createDto = new CreateClientMealDto();
            createDto.setOrderId(orderId);
            createDto.setMealId(mealId);
            createDto.setAmountInOrder(mealData.getAmountInOrder());
            createDto.setIngredients(ingredientDtos != null ? ingredientDtos : List.of());

            save(createDto);
        }
    }

}
