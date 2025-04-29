package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.UserRepository;
import ua.edu.ukma.db.kfc.mappers.UserMapper;
import ua.edu.ukma.db.kfc.rest.model.UserDto;
import ua.edu.ukma.db.kfc.security.PasswordServices;
import ua.edu.ukma.db.kfc.security.SecurityContext;
import ua.edu.ukma.db.kfc.security.SecurityContextHolder;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.UserValidator;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class UserService {

    @Inject
    private UserRepository repository;
    @Inject
    private UserValidator validator;
    @Inject
    private UserMapper mapper;
    @Inject
    private PasswordServices passwordServices;
    @Inject
    private SecurityContextHolder securityContextHolder;

    public int create(String username, String password, UserRoleEnum role) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(passwordServices.hash(password));
        user.setRole(role);
        validator.validForCreate(user);
        return repository.save(user);
    }

    public void delete(int id) {
        repository.delete(id);
    }

    public UserDto getCurrent() {
        SecurityContext context = securityContextHolder.getContext();
        UserEntity user = repository.findByUsername(context.getUsername()).orElseThrow(() -> new NotAuthorizedException("Bearer"));
        return mapper.toResponse(user);
    }

    public boolean checkUserExists(String username) {
        return repository.existsByUsername(username);
    }

    public void disableUser(Integer userId) {
        UserEntity user = repository.findById(userId).orElseThrow(NotFoundException::new);
        validator.validForDisableUser(user);
        repository.disableUser(userId);
    }
}
