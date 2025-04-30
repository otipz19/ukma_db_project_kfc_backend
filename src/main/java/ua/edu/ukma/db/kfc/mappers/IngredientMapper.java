package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.IngredientDto;
import ua.edu.ukma.db.kfc.rest.model.IngredientsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateIngredientDto;

@Mapper(config = MapperConfiguration.class)
public interface IngredientMapper extends IListMapper<IngredientEntity, IngredientDto, IngredientsListDto, UpdateIngredientDto> {}