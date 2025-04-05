package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;

@Mapper(config = MapperConfiguration.class)
public interface EnumsMapper {

    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = MappingConstants.ANY_REMAINING)
    RoleEnum map(String roleName);
    String map(RoleEnum roleName);
}
