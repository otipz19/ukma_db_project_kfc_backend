package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.*;
import ua.edu.ukma.db.kfc.model.entities.ClientMealIngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.ClientMealIngredientDto;
import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface ClientMealIngredientMapper {

    @Mapping(target = "clientMealId", expression = "java(clientMealId)")
    ClientMealIngredientEntity toEntity(@Context int clientMealId, ClientMealIngredientDto dto);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<ClientMealIngredientEntity> toEntities(@Context int clientMealId, List<ClientMealIngredientDto> dtos);

    ClientMealIngredientDto toResponse(ClientMealIngredientEntity entity);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<ClientMealIngredientDto> toResponse(List<ClientMealIngredientEntity> entity);

}
