package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.*;
import ua.edu.ukma.db.kfc.model.entities.*;
import ua.edu.ukma.db.kfc.repositories.EmployeeRepository;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;
import ua.edu.ukma.db.kfc.repositories.MealRepository;
import ua.edu.ukma.db.kfc.repositories.StatisticsRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.rest.model.MealsListDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeesListDto;
import ua.edu.ukma.db.kfc.rest.model.IngredientsListDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeeStatsListDto;
import ua.edu.ukma.db.kfc.rest.model.RestaurantStatsListDto;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class StatisticsService {

    @Inject
    private StatisticsRepository repository;
    @Inject
    private EmployeeRepository employeeRepository;
    @Inject
    private IngredientRepository ingredientRepository;
    @Inject
    private MealRepository mealRepository;
    @Inject
    private MealMapper mealMapper;
    @Inject
    private MealIngredientService mealIngredientService;
    @Inject
    private IngredientMapper ingredientMapper;
    @Inject
    private RestaurantStatsMapper restaurantStatsMapper;
    @Inject
    private EmployeeStatsMapper employeeStatsMapper;
    @Inject
    private EmployeeMapper employeeMapper;

    public MealsListDto getMealPopular() {
        List<NamedEntity> popular = repository.getMealPopular();
        List<Integer> ids = popular.stream().map(NamedEntity::getId).toList();
        List<MealEntity> meals = mealRepository.findByIds(ids);
        Map<Integer, List<MealIngredientEntity>> ingredientsMap = mealIngredientService.getByMealIds(ids);
        return mealMapper.toResponse(meals, ingredientsMap, meals.size());
    }

    public MealsListDto getMealLeastPopular() {
        List<NamedEntity> least = repository.getMealLeastPopular();
        List<Integer> ids = least.stream().map(NamedEntity::getId).toList();
        List<MealEntity> meals = mealRepository.findByIds(ids);
        Map<Integer, List<MealIngredientEntity>> ingredientsMap = mealIngredientService.getByMealIds(ids);
        return mealMapper.toResponse(meals, ingredientsMap, meals.size());
    }


    public EmployeesListDto getTopManagersLastQuarter() {
        List<NamedEntity> top = repository.getTopManagersLastQuarter();
        List<EmployeeEntity> managers = top.stream()
                .map(ne -> employeeRepository.findById(ne.getId())
                        .orElseThrow(() -> new NotFoundException("Manager not found: " + ne.getId())))
                .toList();
        return employeeMapper.toResponse(managers, managers.size());
    }


    public RestaurantStatsListDto getClientMealAveragesByRestaurant(int clientId) {
        List<RestaurantStatsEntity> stats = repository.getClientMealAveragesByRestaurant(clientId);
        if (stats.isEmpty()) {
            throw new NotFoundException("No meal data found for client " + clientId);
        }
        return restaurantStatsMapper.toListDto(stats);
    }

    public EmployeeStatsListDto getEmployeeMealAveragesByRestaurant(int restaurantId) {
        List<EmployeeStatsEntity> stats = repository.getEmployeeMealAveragesByRestaurant(restaurantId);
        if (stats.isEmpty()) {
            throw new NotFoundException("No meal data found for restaurant " + restaurantId);
        }
        return employeeStatsMapper.toListDto(stats);
    }

    public IngredientsListDto getIngredientsOnlyInOrderedMeals() {
        List<NamedEntity> dtos = repository.getIngredientsOnlyInOrderedMeals();
        List<IngredientEntity> ingredients = dtos.stream()
                .map(ne -> ingredientRepository.findById(ne.getId(), false)
                        .orElseThrow(() -> new NotFoundException("Ingredient not found: " + ne.getId())))
                .toList();
        return ingredientMapper.toResponse(ingredients, ingredients.size());
    }

    public MealsListDto getMealsNotOrderedByBonusClients() {
        List<NamedEntity> dtos = repository.getMealsNotOrderedByBonusClients();
        List<Integer> ids = dtos.stream().map(NamedEntity::getId).toList();
        List<MealEntity> meals = mealRepository.findByIds(ids);
        Map<Integer, List<MealIngredientEntity>> ingredientsMap = mealIngredientService.getByMealIds(ids);
        return mealMapper.toResponse(meals, ingredientsMap, meals.size());
    }

    public EmployeesListDto getCashiersAlwaysHighEnergy(int energyThreshold) {
        List<NamedEntity> dtos = repository.getCashiersAlwaysHighEnergy(energyThreshold);
        List<EmployeeEntity> employees = dtos.stream()
                .map(dto -> employeeRepository.findById(dto.getId())
                        .orElseThrow(() -> new NotFoundException("Cashier not found: " + dto.getId())))
                .toList();
        return employeeMapper.toResponse(employees, employees.size());
    }
}