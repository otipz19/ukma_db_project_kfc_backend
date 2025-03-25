package ua.edu.ukma.db.kfc.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.dto.UserDTO;
import ua.edu.ukma.db.kfc.security.Secured;
import ua.edu.ukma.db.kfc.service.UserService;
import java.util.List;

@Path("/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistrationResource {

    @Inject
    private UserService userService;

    @POST
    public Response registerUser(UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return Response.status(Response.Status.CREATED).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error")
                    .build();
        }
    }
    @Secured
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found")
                        .build();
            }
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error")
                    .build();
        }
    }

    @Secured
    @GET
    @Path("/all")
    public Response getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, UserDTO userDTO) {
        try {
            userService.updateUser(id, userDTO);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error")
                    .build();
        }
    }
    @Secured
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Server error")
                    .build();
        }
    }

    @GET
    @Path("/test")
    public Response getTestResponse() {
        String testJson = "{\"message\": \"Registration endpoint is working\"}";
        return Response.ok(testJson, MediaType.APPLICATION_JSON).build();
    }
}
