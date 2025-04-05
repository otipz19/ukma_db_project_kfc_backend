package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.UserPhonesControllerApi;
import ua.edu.ukma.db.kfc.services.UserPhonesService;

import java.util.List;

@ApplicationScoped
public class UserPhonesController implements UserPhonesControllerApi {

    @Inject
    private UserPhonesService userPhonesService;

    @Override
    public Response getUserPhones(Integer userId) {
        return Response.ok(userPhonesService.getUserPhones(userId)).build();
    }

    @Override
    public Response setUserPhones(Integer userId, List<String> phones) {
        userPhonesService.setUserPhones(userId, phones);
        return Response.noContent().build();
    }
}
