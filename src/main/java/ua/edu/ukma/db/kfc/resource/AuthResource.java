package ua.edu.ukma.db.kfc.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.dto.UserDTO;
import ua.edu.ukma.db.kfc.service.AuthService;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/login")
    public Response login(UserDTO userDTO) {
        try {
            String token = authService.login(userDTO.getEmail(), userDTO.getPassword());
            return Response.ok().entity("{\"token\": \"" + token + "\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }
}
