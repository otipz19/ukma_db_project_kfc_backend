package ua.edu.ukma.db.kfc.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
public class SecurityConstants {
    public final static String ROLE_CLAIM = "role";

    @Inject
    @ConfigProperty(name = "security.token.prefix")
    private String tokenPrefix;

    @Inject
    @ConfigProperty(name = "security.token.secret")
    private String tokenSecret;

    @Inject
    @ConfigProperty(name = "security.token.expiration")
    private long tokenExpiration;

    @Inject
    @ConfigProperty(name = "security.refresh.token.expiration")
    private long refreshTokenExpiration;

    @Inject
    @ConfigProperty(name = "security.admin.login")
    private String adminLogin;

    @Inject
    @ConfigProperty(name = "security.admin.password.hash")
    private String adminPasswordHash;
}
