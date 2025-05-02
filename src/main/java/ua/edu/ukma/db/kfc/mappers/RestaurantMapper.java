package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.db.kfc.model.entities.RestaurantEntity;
import ua.edu.ukma.db.kfc.model.helper.RestaurantStatistic;
import ua.edu.ukma.db.kfc.rest.model.*;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface RestaurantMapper extends IListMapper<RestaurantEntity, RestaurantDto, RestaurantsListDto, UpdateRestaurantDto> {

    @Mapping(target = "isDeleted", source = "deleted")
    RestaurantStatisticDto toStatisticResponse(RestaurantStatistic entity);

    RestaurantsStatisticListDto toStatisticResponse(List<RestaurantStatistic> items, long total);
}
