package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import ua.edu.ukma.db.kfc.repositories.UserEmailsRepository;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.UserEmailsValidator;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class UserEmailsService {

    @Inject
    private UserEmailsValidator validator;
    @Inject
    private UserEmailsRepository repository;

    public List<String> getUserEmails(int userId) {
        validator.validForViewEmails(userId);
        return repository.getUserEmails(userId);
    }

    public void setUserEmails(int userId, List<String> emails) {
        validator.validForSetEmails(userId, emails);
        repository.clearUserEmails(userId);
        repository.setUserEmails(userId, new HashSet<>(emails));
    }

    public boolean checkUserEmailExists(String email) {
        return repository.exists(email);
    }
}
