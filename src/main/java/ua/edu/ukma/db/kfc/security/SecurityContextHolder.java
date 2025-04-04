package ua.edu.ukma.db.kfc.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;

@RequestScoped
public class SecurityContextHolder {

    private SecurityContext context;

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

    public boolean hasRole(RoleEnum... roles) {
        authorized();
        for (RoleEnum role : roles) {
            if (context.getUserRole() == role)
                return true;
        }
        return false;
    }

    public void requireRole(RoleEnum... roles) {
        if (!hasRole(roles))
            throw new ForbiddenException("Forbidden");
    }
}
