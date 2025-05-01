package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.rest.model.EmployeeDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeeHiringDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeesListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateEmployeeDto;

@Mapper(config = MapperConfiguration.class)
public interface EmployeeMapper extends IListMapper<EmployeeEntity, EmployeeDto, EmployeesListDto, UpdateEmployeeDto> {

    @Mapping(target = "userId", ignore = true)
    void toEntity(UpdateEmployeeDto dto, @MappingTarget EmployeeEntity entity);

    @Mapping(target = "userId", ignore = true)
    void toEntity(EmployeeHiringDto dto, @MappingTarget EmployeeEntity entity);
}
