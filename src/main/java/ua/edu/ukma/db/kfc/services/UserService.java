package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.repositories.UserRepository;
import ua.edu.ukma.db.kfc.mappers.UserMapper;
import ua.edu.ukma.db.kfc.rest.model.UserDto;
import ua.edu.ukma.db.kfc.security.SecurityContext;
import ua.edu.ukma.db.kfc.security.SecurityContextHolder;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class UserService {

    @Inject
    private UserRepository repository;

    @Inject
    private UserMapper mapper;

    @Inject
    private SecurityContextHolder securityContextHolder;

    public UserDto getCurrent() {
        SecurityContext context = securityContextHolder.getContext();
        UserEntity user = repository.findByEmail(context.getUserName()).orElseThrow();
        return mapper.toDto(user);
    }
}
