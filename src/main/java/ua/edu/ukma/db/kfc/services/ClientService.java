package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.ClientMapper;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;
import ua.edu.ukma.db.kfc.repositories.ClientRepository;
import ua.edu.ukma.db.kfc.rest.model.ClientDto;
import ua.edu.ukma.db.kfc.rest.model.ClientRegistrationDto;
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
        int userId = userService.create(clientRegistrationDto.getUsername(), clientRegistrationDto.getPassword(), RoleEnum.CLIENT);
        ClientEntity client = new ClientEntity();
        client.setUserId(userId);
        mapper.toEntity(clientRegistrationDto, client);
        validator.validForCreate(client);
        repository.save(client);
        return userId;
    }

    public ClientDto getClientByUserId(int userId) {
        ClientEntity entity = repository.findByUserId(userId).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        return mapper.toResponse(entity);
    }

    public List<ClientDto> getAllClients() {
        List<ClientEntity> entities = repository.findAll();
        validator.validForView(entities);
        return mapper.toResponse(entities);
    }
}
