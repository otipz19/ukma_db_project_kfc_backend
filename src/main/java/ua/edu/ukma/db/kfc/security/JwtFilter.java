package ua.edu.ukma.db.kfc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import ua.edu.ukma.db.kfc.auth.JwtUtil;

import jakarta.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!isSecured(resourceInfo.getResourceMethod(), resourceInfo.getResourceClass())) {
            return;
        }
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Authorization header must be provided").build());
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        try {
            Claims claims = JwtUtil.validateToken(token);
            requestContext.setProperty("userEmail", claims.getSubject());
        } catch (JwtException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired token").build());
        }
    }

    private boolean isSecured(Method method, Class<?> resourceClass) {
        if (method == null) return false;

        return method.isAnnotationPresent(Secured.class) ||
                (resourceClass != null && resourceClass.isAnnotationPresent(Secured.class));
    }
}