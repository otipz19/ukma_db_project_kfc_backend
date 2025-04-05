package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.ClientControllerApi;
import ua.edu.ukma.db.kfc.rest.model.ClientRegistrationDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateClientDto;
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
    public Response getClientByUserId(Integer userId) {
        return Response.ok(clientService.getClientByUserId(userId)).build();
    }

    @Override
    public Response getAllClients() {
        return Response.ok(clientService.getAllClients()).build();
    }

    @Override
    public Response updateClientByUserId(Integer userId, UpdateClientDto updateClientDto) {
        clientService.updateClientByUserId(userId, updateClientDto);
        return Response.noContent().build();
    }

    @Override
    public Response deleteClientByUserId(Integer userId) {
        clientService.deleteClientByUserId(userId);
        return Response.noContent().build();
    }
}
