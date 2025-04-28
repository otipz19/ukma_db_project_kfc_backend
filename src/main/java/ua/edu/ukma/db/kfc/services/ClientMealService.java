package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.ClientMealMapper;
import ua.edu.ukma.db.kfc.model.entities.*;
import ua.edu.ukma.db.kfc.repositories.ClientMealRepository;
import ua.edu.ukma.db.kfc.rest.model.ClientMealIngredientDto;
import ua.edu.ukma.db.kfc.services.ClientMealIngredientService.DbCache;
import ua.edu.ukma.db.kfc.services.ClientMealIngredientService.IngredientsMergeResult;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.ClientMealValidator;
import ua.edu.ukma.db.kfc.rest.model.ClientMealDto;
import ua.edu.ukma.db.kfc.rest.model.CreateClientMealDto;

import java.util.*;

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

    public List<ClientMealDto> getAllClientMeals(Integer orderId) {
        List<ClientMealEntity> entities = repository.findAll(orderId);
        validator.validForView(entities);
        Map<Integer, List<ClientMealIngredientEntity>> ingredientsMap = clientMealIngredientService.getByClientMealIds(
                entities.stream().map(ClientMealEntity::getId).toList()
        );
        return mapper.toResponse(entities, ingredientsMap);
    }

    public ClientMealDto getClientMealById(int id) {
        ClientMealEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        List<ClientMealIngredientEntity> ingredients = clientMealIngredientService.getByClientMealId(entity.getId());
        return mapper.toResponse(entity, ingredients);
    }

    public void createClientMeals(int orderId, List<CreateClientMealDto> clientMealsDtos) {
        List<ClientMealEntity> clientMeals = new ArrayList<>();
        List<List<ClientMealIngredientDto>> clientMealsIngredients = new ArrayList<>();
        DbCache dbCache = clientMealIngredientService.fetchClientMealsIngredientsData(
                clientMealsDtos.stream().map(CreateClientMealDto::getMealId).distinct().toList()
        );
        for (CreateClientMealDto clientMealDto : clientMealsDtos) {
            ClientMealEntity clientMealEntity = mapper.toEntity(orderId, clientMealDto);
            IngredientsMergeResult merged = clientMealIngredientService.merge(clientMealDto.getMealId(), clientMealDto.getIngredientOverrides(), dbCache);
            clientMealIngredientService.calculateDerivedAttributes(clientMealEntity, merged.merged(), dbCache);
            clientMeals.add(clientMealEntity);
            clientMealsIngredients.add(merged.overridden());
        }
        validator.validForCreate(clientMeals);
        List<Integer> clientMealsIds = repository.saveAll(clientMeals);
        for (int i = 0; i < clientMealsIds.size(); ++i)
            clientMealIngredientService.saveAll(clientMealsIds.get(i), clientMealsIngredients.get(i));
    }
}
