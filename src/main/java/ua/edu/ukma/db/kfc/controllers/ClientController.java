package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.ClientControllerApi;
import ua.edu.ukma.db.kfc.rest.model.ClientRegistrationDto;
import ua.edu.ukma.db.kfc.services.ClientService;

@ApplicationScoped
public class ClientController implements ClientControllerApi {

    @Inject
    private ClientService clientService;

    @Override
    public Response registerClient(ClientRegistrationDto clientRegistrationDto) {
        return Response.ok(clientService.registerClient(clientRegistrationDto)).build();
    }

    @Override
    public Response getClientById(Integer clientId) {
        return Response.ok(clientService.getClient(clientId)).build();
    }

    @Override
    public Response getAllClients() {
        return Response.ok(clientService.getAllClients()).build();
    }
}
