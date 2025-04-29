package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.UserControllerApi;
import ua.edu.ukma.db.kfc.services.UserService;

@ApplicationScoped
public class UserController implements UserControllerApi {

    @Inject
    private UserService userService;

    @Override
    public Response getCurrentUser() {
        return Response.ok().entity(userService.getCurrent()).build();
    }

    @Override
    public Response checkUserExists(String username) {
        return Response.ok(userService.checkUserExists(username)).build();
    }

    @Override
    public Response disableUser(Integer userId) {
        userService.disableUser(userId);
        return Response.noContent().build();
    }
}
