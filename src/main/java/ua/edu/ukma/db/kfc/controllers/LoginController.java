package ua.edu.ukma.db.kfc.controllers;

import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.LoginControllerApi;
import ua.edu.ukma.db.kfc.rest.model.LoginRequestDto;
import ua.edu.ukma.db.kfc.rest.model.ResetTokenRequestDto;

public class LoginController implements LoginControllerApi {

    @Override
    public Response loginUser(LoginRequestDto loginRequestDto) {
        return null;
    }

    @Override
    public Response resetToken(ResetTokenRequestDto resetTokenRequestDto) {
        return null;
    }
}
