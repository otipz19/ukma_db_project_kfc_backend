package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.IngredientMapper;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.IngredientDto;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;
import ua.edu.ukma.db.kfc.rest.model.UpdateIngredientDto;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.IngredientValidator;

import java.util.List;

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
    private MealIngredientService mealIngredientService;

    public List<IngredientDto> getAllIngredients(List<Integer> ids) {
        List<IngredientEntity> ingredients = (ids == null || ids.isEmpty()) ? repository.findAll() : repository.findByIds(ids);
        validator.validForView(ingredients);
        return mapper.toResponse(ingredients);
    }

    public IngredientDto getIngredientById(int id) {
        IngredientEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        return mapper.toResponse(entity);
    }

    public IngredientDto getIngredientByTitle(String title) {
        IngredientEntity entity = repository.findByTitle(title).orElseThrow(NotFoundException::new);
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
        IngredientEntity ingredient = repository.findById(id).orElseThrow(NotFoundException::new);
        IngredientEntity copy = ingredient.copy();
        mapper.toEntity(dto, ingredient);
        validator.validForUpdate(ingredient);

        repository.delete(id);
        int newId = repository.save(ingredient);
        ingredient.setId(newId);
        mealIngredientService.updateMealsContainingIngredient(ingredient, copy);

        return newId;
    }

    public void deleteIngredient(int id) {
        IngredientEntity entity = repository.findById(id).orElseThrow(NotFoundException::new);
        validator.validForDelete(entity);
        repository.delete(id);
    }
}