package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotAuthorizedException;
import ua.edu.ukma.db.kfc.configuration.SecurityConstants;
import ua.edu.ukma.db.kfc.model.entities.UserEntity;
import ua.edu.ukma.db.kfc.repositories.UserRepository;
import ua.edu.ukma.db.kfc.rest.model.LoginRequestDto;
import ua.edu.ukma.db.kfc.rest.model.LoginResponseDto;
import ua.edu.ukma.db.kfc.rest.model.ResetTokenRequestDto;
import ua.edu.ukma.db.kfc.rest.model.ResetTokenResponseDto;
import ua.edu.ukma.db.kfc.security.JwtServices;
import ua.edu.ukma.db.kfc.security.PasswordServices;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;

import java.util.Optional;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class AuthenticationService {

    @Inject
    private UserRepository userRepository;
    @Inject
    private PasswordServices passwordServices;
    @Inject
    private JwtServices jwtServices;
    @Inject
    private SecurityConstants securityConstants;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Optional<UserEntity> user = userRepository.findByEmail(loginRequestDto.getUsername());
        if (user.isEmpty() || !passwordServices.check(loginRequestDto.getPassword(), getPasswordHash(user.get())))
            throw new NotAuthorizedException("Authorization failed");
        String token = jwtServices.generateToken(user.get());
        String refreshToken = jwtServices.generateRefreshToken(user.get());
        return new LoginResponseDto(token, refreshToken);
    }

    private String getPasswordHash(UserEntity user) {
        if (user.getEmail().equals(securityConstants.getAdminLogin()))
            return securityConstants.getAdminPasswordHash();
        return user.getPasswordHash();
    }

    public ResetTokenResponseDto resetToken(ResetTokenRequestDto resetTokenRequestDto) {
        try {
            String subject = jwtServices.verifyRefreshToken(resetTokenRequestDto.getRefreshToken());
            UserEntity user = userRepository.findByEmail(subject).orElseThrow();
            String token = jwtServices.generateToken(user);
            return new ResetTokenResponseDto(token);
        } catch (Exception e) {
            throw new NotAuthorizedException("Authorization failed");
        }
    }
}
