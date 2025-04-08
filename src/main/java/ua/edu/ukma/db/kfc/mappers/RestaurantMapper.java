package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;
import ua.edu.ukma.db.kfc.rest.model.RestaurantDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateRestaurantDto;

@Mapper(config = MapperConfiguration.class)
public interface RestaurantMapper extends IMapper<RestaurantEntity, RestaurantDto, UpdateRestaurantDto> {
}
