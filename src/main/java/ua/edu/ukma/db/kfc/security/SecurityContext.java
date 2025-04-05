package ua.edu.ukma.db.kfc.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.edu.ukma.db.kfc.model.enums.RoleEnum;

@Getter
@RequiredArgsConstructor
public class SecurityContext {

    private final String username;
    private final RoleEnum userRole;

}
