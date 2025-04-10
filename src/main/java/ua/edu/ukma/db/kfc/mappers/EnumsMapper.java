package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import ua.edu.ukma.db.kfc.model.enums.EmployeePositionEnum;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.rest.model.EmployeePositionDto;
import ua.edu.ukma.db.kfc.rest.model.UserRoleDto;

@Mapper(config = MapperConfiguration.class)
public interface EnumsMapper {

    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = MappingConstants.ANY_REMAINING)
    UserRoleEnum mapToRole(String roleName);

    String mapToSting(UserRoleEnum role);

    UserRoleDto map(UserRoleEnum role);

    @ValueMapping(target = "TOP_MANAGER", source = "ADMIN")
    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = MappingConstants.ANY_REMAINING)
    EmployeePositionEnum mapToPosition(String roleName);

    @ValueMapping(target = "ADMIN", source = "TOP_MANAGER")
    UserRoleEnum mapToRole(EmployeePositionDto role);

    EmployeePositionDto map(EmployeePositionEnum role);

    EmployeePositionEnum map(EmployeePositionDto role);
}
