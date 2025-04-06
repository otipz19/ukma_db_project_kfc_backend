package ua.edu.ukma.db.kfc.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.edu.ukma.db.kfc.model.enums.UserRoleEnum;

@Getter
@RequiredArgsConstructor
public class SecurityContext {

    private final String username;
    private final UserRoleEnum userRole;

}
