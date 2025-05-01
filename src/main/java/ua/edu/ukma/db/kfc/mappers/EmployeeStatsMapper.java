package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.EmployeeStatsEntity;
import ua.edu.ukma.db.kfc.rest.model.EmployeeStatsDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeeStatsListDto;

@Mapper(config = MapperConfiguration.class)
public interface EmployeeStatsMapper {
    EmployeeStatsDto toDto(EmployeeStatsEntity entity);
    default EmployeeStatsListDto toListDto(java.util.List<EmployeeStatsEntity> entities) {
        EmployeeStatsListDto dto = new EmployeeStatsListDto();
        dto.setItems(entities.stream().map(this::toDto).toList());
        return dto;
    }
}
