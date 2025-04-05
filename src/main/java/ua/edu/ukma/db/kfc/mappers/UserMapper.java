package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.rest.model.UserDto;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper extends IMapper<UserEntity, UserDto, UserDto> {
}
