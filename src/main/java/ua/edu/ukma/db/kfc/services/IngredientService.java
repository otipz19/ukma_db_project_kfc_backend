package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.IngredientMapper;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.IngredientDto;
import ua.edu.ukma.db.kfc.rest.model.IngredientUpsertDto;
import ua.edu.ukma.db.kfc.repositories.IngredientRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.IngredientValidator;

import java.util.List;
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

    public List<IngredientDto> getAllIngredients() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public IngredientDto getIngredientById(int id) {
        IngredientEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient with id " + id + " not found"));
        return mapper.toResponse(entity);
    }

    public IngredientDto getIngredientByTitle(String title) {
        IngredientEntity entity = repository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException("Ingredient with title " + title + " not found"));
        return mapper.toResponse(entity);
    }

    public void createIngredient(IngredientUpsertDto dto) {
        IngredientEntity entity = new IngredientEntity();
        mapper.toEntity(dto, entity);
        validator.createValidate(entity);
        repository.save(entity);
    }

    public void deleteIngredient(int id) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
        repository.deactivate(id);
    }

    public void updateIngredient(Integer id, IngredientUpsertDto dto) {
        repository.findById(id)
                .orElseThrow(NotFoundException::new);

        IngredientEntity newEntity = new IngredientEntity();
        mapper.toEntity(dto, newEntity);
        validator.createValidate(newEntity);
        repository.save(newEntity);

        repository.deactivate(id);
    }
}