package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.rest.model.ClientDto;
import ua.edu.ukma.db.kfc.rest.model.ClientsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateClientDto;

@Mapper(config = MapperConfiguration.class)
public interface ClientMapper extends IListMapper<ClientEntity, ClientDto, ClientsListDto, UpdateClientDto> {

    @Mapping(target = "userId", ignore = true)
    void toEntity(UpdateClientDto dto, @MappingTarget ClientEntity entity);
}
