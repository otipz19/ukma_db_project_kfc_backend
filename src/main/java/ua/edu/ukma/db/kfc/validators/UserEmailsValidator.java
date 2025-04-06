package ua.edu.ukma.db.kfc.validators;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.UserEmailsRepository;
import ua.edu.ukma.db.kfc.repositories.UserRepository;
import ua.edu.ukma.db.kfc.security.SecurityContextHolder;

import java.util.List;

@ApplicationScoped
public class UserEmailsValidator {

    @Inject
    private SecurityContextHolder securityContextHolder;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserEmailsRepository userEmailsRepository;

    public void validForViewEmails(int userId) {
        if (actionForThemself(userId)) return;
        securityContextHolder.requireRole(UserRoleEnum.ADMIN, UserRoleEnum.MANAGER);
    }

    public void validForSetEmails(int userId, List<String> emails) {
        if (actionForThemself(userId)) return;
        securityContextHolder.requireRole(UserRoleEnum.ADMIN);
        if (userEmailsRepository.existsAnotherUserWithEmail(userId, emails))
            throw new BadRequestException("This email is already in use");
    }

    private boolean actionForThemself(int userId) {
        String currentUser = securityContextHolder.getContext().getUsername();
        String requestedUser = userRepository.findById(userId).orElseThrow(NotFoundException::new).getUsername();
        return currentUser.equals(requestedUser);
    }
}
