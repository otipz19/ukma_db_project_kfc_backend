package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.ukma.db.kfc.model.entities.ClientMealEntity;
import ua.edu.ukma.db.kfc.model.entities.ClientMealIngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.ClientMealDto;
import ua.edu.ukma.db.kfc.rest.model.CreateClientMealDto;

import java.util.List;
import java.util.Map;

@Mapper(config = MapperConfiguration.class, uses = ClientMealIngredientMapper.class)
public interface ClientMealMapper {

    ClientMealDto toResponse(ClientMealEntity meal, List<ClientMealIngredientEntity> ingredients);

    default List<ClientMealDto> toResponse(List<ClientMealEntity> entities, Map<Integer, List<ClientMealIngredientEntity>> ingredientsMap) {
        if (entities == null || ingredientsMap == null) return List.of();
        return entities.stream()
                .map(entity -> toResponse(entity, ingredientsMap.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    void toEntity(CreateClientMealDto dto, @MappingTarget ClientMealEntity entity);

}