package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.UserEmailsControllerApi;
import ua.edu.ukma.db.kfc.services.UserEmailsService;

import java.util.List;

@ApplicationScoped
public class UserEmailsController implements UserEmailsControllerApi {

    @Inject
    private UserEmailsService userEmailsService;

    @Override
    public Response getUserEmails(Integer userId) {
        return Response.ok(userEmailsService.getUserEmails(userId)).build();
    }

    @Override
    public Response setUserEmails(Integer userId, List<String> emails) {
        userEmailsService.setUserEmails(userId, emails);
        return Response.noContent().build();
    }
}
