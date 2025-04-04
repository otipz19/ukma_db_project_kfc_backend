package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;

@Mapper(config = MapperConfiguration.class)
public interface EnumsMapper {

    RoleEnum map(String roleName);
}
