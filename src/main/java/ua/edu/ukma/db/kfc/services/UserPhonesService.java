package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import ua.edu.ukma.db.kfc.repositories.UserPhonesRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.UserPhonesValidator;

import java.util.List;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class UserPhonesService {

    @Inject
    private UserPhonesValidator validator;
    @Inject
    private UserPhonesRepository repository;

    public List<String> getUserPhones(int userId) {
        validator.validForViewPhones(userId);
        return repository.getUserPhones(userId);
    }

    public void setUserPhones(int userId, List<String> phones) {
        validator.validForSetPhones(userId, phones);
        repository.clearUserPhones(userId);
        repository.setUserPhones(userId, phones);
    }
}
