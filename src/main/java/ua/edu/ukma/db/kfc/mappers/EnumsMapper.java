package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.rest.model.UserRoleDto;

@Mapper(config = MapperConfiguration.class)
public interface EnumsMapper {

    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = MappingConstants.ANY_REMAINING)
    UserRoleEnum map(String roleName);
    String mapToSting(UserRoleEnum role);
    UserRoleDto map(UserRoleEnum role);
}
