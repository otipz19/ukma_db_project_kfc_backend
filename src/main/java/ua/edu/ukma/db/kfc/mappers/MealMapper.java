package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.MealEntity;
import ua.edu.ukma.db.kfc.rest.model.MealDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateMealDto;

@Mapper(config = MapperConfiguration.class)
public interface MealMapper extends IMapper<MealEntity, MealDto, UpdateMealDto> {
}