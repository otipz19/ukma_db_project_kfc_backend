package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.*;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.MealIngredientDto;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface MealIngredientMapper {

    @Mapping(target = "mealId", expression = "java(mealId)")
    @Mapping(target = "fixated", source = "dto.isFixated")
    MealIngredientEntity toEntity(@Context int mealId, MealIngredientDto dto);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<MealIngredientEntity> toEntities(@Context int mealId, List<MealIngredientDto> dtos);

    @Mapping(target = "isFixated", source = "fixated")
    MealIngredientDto toResponse(MealIngredientEntity entity);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<MealIngredientDto> toResponse(List<MealIngredientEntity> entity);

}
