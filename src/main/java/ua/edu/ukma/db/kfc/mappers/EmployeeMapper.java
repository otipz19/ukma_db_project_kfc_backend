package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.rest.model.EmployeeDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeesListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateEmployeeDto;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface EmployeeMapper extends IMapper<EmployeeEntity, EmployeeDto, UpdateEmployeeDto> {

    EmployeesListDto toResponse(List<EmployeeEntity> items, long total);
}
