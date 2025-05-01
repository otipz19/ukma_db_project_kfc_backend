package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.*;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.model.entities.MealIngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.CreateMealDto;
import ua.edu.ukma.db.kfc.rest.model.MealDto;
import ua.edu.ukma.db.kfc.rest.model.MealsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateMealDto;

import java.util.List;
import java.util.Map;

@Mapper(config = MapperConfiguration.class, uses = MealIngredientMapper.class)
public interface MealMapper {

    MealDto toResponse(MealEntity meal, List<MealIngredientEntity> ingredients);

    default List<MealDto> toResponse(List<MealEntity> entities, Map<Integer, List<MealIngredientEntity>> ingredientsMap) {
        if (entities == null || ingredientsMap == null) return List.of();
        return entities.stream()
                .map(entity -> toResponse(entity, ingredientsMap.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    default MealsListDto toResponse(List<MealEntity> entities, Map<Integer, List<MealIngredientEntity>> ingredientsMap, long total) {
        return new MealsListDto(toResponse(entities, ingredientsMap), total);
    }

    @Mapping(target = "id", ignore = true)
    void toEntity(UpdateMealDto dto, @MappingTarget MealEntity entity);

    @Mapping(target = "id", ignore = true)
    MealEntity toEntity(CreateMealDto dto);
}