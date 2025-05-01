package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.CreateIngredientDto;
import ua.edu.ukma.db.kfc.rest.model.IngredientDto;
import ua.edu.ukma.db.kfc.rest.model.IngredientsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateIngredientDto;

@Mapper(config = MapperConfiguration.class)
public interface IngredientMapper extends IListMapper<IngredientEntity, IngredientDto, IngredientsListDto, UpdateIngredientDto> {

    @Mapping(target = "id", ignore = true)
    IngredientEntity toEntity(CreateIngredientDto dto);
}