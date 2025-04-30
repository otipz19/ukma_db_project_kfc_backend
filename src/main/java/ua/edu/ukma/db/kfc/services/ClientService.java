package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.filters.ClientsFilter;
import ua.edu.ukma.db.kfc.mappers.ClientMapper;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.ClientRepository;
import ua.edu.ukma.db.kfc.rest.model.*;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.ClientValidator;

import java.util.List;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class ClientService {

    @Inject
    private UserService userService;
    @Inject
    private ClientRepository repository;
    @Inject
    private ClientMapper mapper;
    @Inject
    private ClientValidator validator;

    public int registerClient(ClientRegistrationDto clientRegistrationDto) {
        int userId = userService.create(clientRegistrationDto.getUsername(), clientRegistrationDto.getPassword(), UserRoleEnum.CLIENT);
        ClientEntity client = new ClientEntity();
        client.setUserId(userId);
        mapper.toEntity(clientRegistrationDto, client);
        validator.validForCreate(client);
        repository.save(client);
        return userId;
    }

    public Integer getIdByUserId(Integer userId) {
        if (userId == null) return null;
        return repository.findIdByUserId(userId).orElseThrow(NotFoundException::new);
    }

    public ClientDto getClientByUserId(int userId) {
        ClientEntity entity = repository.findByUserId(userId).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        return mapper.toResponse(entity);
    }

    public ClientsListDto getClientsByFilter(ClientsFilterDto filterDto) {
        ClientsFilter filter = new ClientsFilter(filterDto);
        List<ClientEntity> entities = repository.findByFilter(filter);
        validator.validForView(entities);
        long total = repository.countByFilter(filter);
        return mapper.toResponse(entities, total);
    }

    public void updateClientByUserId(int userId, UpdateClientDto updateClientDto) {
        ClientEntity entity = repository.findByUserId(userId).orElseThrow(NotFoundException::new);
        mapper.toEntity(updateClientDto, entity);
        validator.validForUpdate(entity);
        repository.update(entity);
    }

    public void deleteClientByUserId(int userId) {
        ClientEntity entity = repository.findByUserId(userId).orElseThrow(NotFoundException::new);
        validator.validForDelete(entity);
        repository.deleteByUserId(userId);
        userService.delete(userId);
    }
}
