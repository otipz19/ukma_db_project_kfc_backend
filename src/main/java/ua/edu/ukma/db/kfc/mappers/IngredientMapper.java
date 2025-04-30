package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.IngredientEntity;
import ua.edu.ukma.db.kfc.rest.model.IngredientDto;
import ua.edu.ukma.db.kfc.rest.model.IngredientsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateIngredientDto;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface IngredientMapper extends IMapper<IngredientEntity, IngredientDto, UpdateIngredientDto> {

    IngredientsListDto toResponse(List<IngredientEntity> items, long total);
}