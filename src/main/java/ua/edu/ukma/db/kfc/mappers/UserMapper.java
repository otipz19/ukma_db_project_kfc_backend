package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.rest.model.UserDto;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper extends IMapper<UserEntity, UserDto> {

    @Override
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void toEntity(UserDto dto, @MappingTarget UserEntity entity);
}
