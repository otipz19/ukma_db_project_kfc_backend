package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.filters.RestaurantsFilter;
import ua.edu.ukma.db.kfc.mappers.RestaurantMapper;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;
import ua.edu.ukma.db.kfc.repositories.RestaurantRepository;
import ua.edu.ukma.db.kfc.rest.model.RestaurantDto;
import ua.edu.ukma.db.kfc.rest.model.RestaurantsFilterDto;
import ua.edu.ukma.db.kfc.rest.model.RestaurantsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateRestaurantDto;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.RestaurantValidator;

import java.util.List;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class RestaurantService {

    @Inject
    private RestaurantRepository repository;
    @Inject
    private RestaurantValidator validator;
    @Inject
    private RestaurantMapper mapper;

    public RestaurantsListDto getRestaurantsByFilter(RestaurantsFilterDto filterDto) {
        RestaurantsFilter filter = new RestaurantsFilter(filterDto);
        List<RestaurantEntity> restaurants = repository.findByFilter(filter);
        validator.validForView(restaurants);
        long total = repository.countByFilter(filter);
        return mapper.toResponse(restaurants, total);
    }

    public RestaurantDto getRestaurantById(int restaurantId, boolean requireNotDeleted) {
        RestaurantEntity restaurant = repository.findById(restaurantId, requireNotDeleted).orElseThrow(NotFoundException::new);
        validator.validForView(restaurant);
        return mapper.toResponse(restaurant);
    }

    public int createRestaurant(UpdateRestaurantDto updateRestaurantDto) {
        RestaurantEntity restaurant = new RestaurantEntity();
        mapper.toEntity(updateRestaurantDto, restaurant);
        validator.validForCreate(restaurant);
        return repository.save(restaurant);
    }

    public void updateRestaurantById(int restaurantId, UpdateRestaurantDto updateRestaurantDto) {
        RestaurantEntity restaurant = repository.findById(restaurantId).orElseThrow(NotFoundException::new);
        mapper.toEntity(updateRestaurantDto, restaurant);
        validator.validForUpdate(restaurant);
        repository.update(restaurant);
    }

    public void deleteRestaurantById(int restaurantId) {
        RestaurantEntity restaurant = repository.findById(restaurantId).orElseThrow(NotFoundException::new);
        validator.validForDelete(restaurant);
        repository.delete(restaurantId);
    }
}
