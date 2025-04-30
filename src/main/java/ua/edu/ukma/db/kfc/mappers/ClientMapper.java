package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.rest.model.ClientDto;
import ua.edu.ukma.db.kfc.rest.model.ClientsListDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateClientDto;

@Mapper(config = MapperConfiguration.class)
public interface ClientMapper extends IListMapper<ClientEntity, ClientDto, ClientsListDto, UpdateClientDto> {}
