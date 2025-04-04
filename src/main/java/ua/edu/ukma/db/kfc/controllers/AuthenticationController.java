package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.AuthenticationControllerApi;
import ua.edu.ukma.db.kfc.rest.model.LoginRequestDto;
import ua.edu.ukma.db.kfc.rest.model.ResetTokenRequestDto;
import ua.edu.ukma.db.kfc.services.AuthenticationService;

@ApplicationScoped
public class AuthenticationController implements AuthenticationControllerApi {

    @Inject
    private AuthenticationService authenticationService;

    @Override
    public Response loginUser(LoginRequestDto loginRequestDto) {
        return Response.ok(authenticationService.login(loginRequestDto)).build();
    }

    @Override
    public Response resetToken(ResetTokenRequestDto resetTokenRequestDto) {
        return Response.ok(authenticationService.resetToken(resetTokenRequestDto)).build();
    }
}
