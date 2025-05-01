package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.RestaurantStatsEntity;
import ua.edu.ukma.db.kfc.rest.model.RestaurantStatsDto;
import ua.edu.ukma.db.kfc.rest.model.RestaurantStatsListDto;

@Mapper(config = MapperConfiguration.class)
public interface RestaurantStatsMapper {
    RestaurantStatsDto toDto(RestaurantStatsEntity entity);
    default RestaurantStatsListDto toListDto(java.util.List<RestaurantStatsEntity> entities) {
        RestaurantStatsListDto dto = new RestaurantStatsListDto();
        dto.setItems(entities.stream().map(this::toDto).toList());
        return dto;
    }
}
