package ua.edu.ukma.db.kfc.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ua.edu.ukma.db.kfc.configuration.SecurityConstants;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@ApplicationScoped
public class JwtServices {

    @Inject
    private SecurityConstants securityConstants;
    @Inject
    private EnumsMapper enumsMapper;

    public String generateToken(UserEntity user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date(TimeUtils.getCurrentTimeUTC()))
                .withExpiresAt(new Date(TimeUtils.getCurrentTimeUTC() + securityConstants.getTokenExpiration()))
                .withClaim(SecurityConstants.ROLE_CLAIM, user.getRole().toString())
                .sign(HMAC512(securityConstants.getTokenSecret().getBytes()));
    }

    public SecurityContext verifyToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(securityConstants.getTokenSecret().getBytes()))
                .withClaimPresence(SecurityConstants.ROLE_CLAIM)
                .build()
                .verify(removePrefix(token));
        return new SecurityContext(jwt.getSubject(), enumsMapper.map(jwt.getClaim(SecurityConstants.ROLE_CLAIM).asString()));
    }

    public String generateRefreshToken(UserEntity user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date(TimeUtils.getCurrentTimeUTC()))
                .withExpiresAt(new Date(TimeUtils.getCurrentTimeUTC() + securityConstants.getRefreshTokenExpiration()))
                .sign(HMAC512(securityConstants.getTokenSecret().getBytes()));
    }

    public String verifyRefreshToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(securityConstants.getTokenSecret().getBytes()))
                .build()
                .verify(removePrefix(token));
        return jwt.getSubject();
    }

    private String removePrefix(String token) {
        return token.replace(securityConstants.getTokenPrefix(), "");
    }
}
