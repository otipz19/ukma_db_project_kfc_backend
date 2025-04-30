package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;
import ua.edu.ukma.db.kfc.rest.model.RestaurantDto;
import ua.edu.ukma.db.kfc.rest.model.RestaurantsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateRestaurantDto;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface RestaurantMapper extends IListMapper<RestaurantEntity, RestaurantDto, RestaurantsListDto, UpdateRestaurantDto> {}
