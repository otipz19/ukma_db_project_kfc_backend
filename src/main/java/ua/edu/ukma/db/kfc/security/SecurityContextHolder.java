package ua.edu.ukma.db.kfc.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import ua.edu.ukma.db.kfc.model.entities.ClientEntity;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;
import ua.edu.ukma.db.kfc.repositories.ClientRepository;
import ua.edu.ukma.db.kfc.repositories.EmployeeRepository;

import java.util.Optional;

@RequestScoped
public class SecurityContextHolder {

    @Inject
    private ClientRepository clientRepository;
    @Inject
    private EmployeeRepository employeeRepository;

    private SecurityContext context;
    private Optional<ClientEntity> currentClient;
    private Optional<EmployeeEntity> currentEmployee;

    void setContext(SecurityContext context) {
        this.context = context;
    }

    public void authorized() {
        if (context == null)
            throw new NotAuthorizedException("Bearer");
    }

    public SecurityContext getContext() {
        authorized();
        return context;
    }

    public boolean hasRole(UserRoleEnum... roles) {
        authorized();
        for (UserRoleEnum role : roles) {
            if (context.getUserRole() == role)
                return true;
        }
        return false;
    }

    public void requireRole(UserRoleEnum... roles) {
        if (!hasRole(roles))
            throw new ForbiddenException();
    }

    public ClientEntity getCurrentClientOrThrow() {
        if (currentClient == null)
            currentClient = fetchCurrentClient();
        return currentClient.orElseThrow(ForbiddenException::new);
    }

    private Optional<ClientEntity> fetchCurrentClient() {
        authorized();
        return clientRepository.findByUsername(context.getUsername());
    }

    public EmployeeEntity getCurrentEmployeeOrThrow() {
        if (currentEmployee == null)
            currentEmployee = fetchCurrentEmployee();
        return currentEmployee.orElseThrow(ForbiddenException::new);
    }

    private Optional<EmployeeEntity> fetchCurrentEmployee() {
        authorized();
        return employeeRepository.findByUsername(context.getUsername());
    }
}
