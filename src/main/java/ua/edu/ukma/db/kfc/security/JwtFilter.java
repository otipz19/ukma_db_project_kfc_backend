package ua.edu.ukma.db.kfc.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import ua.edu.ukma.db.kfc.configuration.SecurityConstants;

import java.io.IOException;

@WebFilter("/api/*")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JwtFilter extends GenericFilter {

    private final JwtServices jwtServices;
    private final SecurityContextHolder securityContextHolder;
    private final SecurityConstants securityConstants;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS)) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith(securityConstants.getTokenPrefix())) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            SecurityContext context = jwtServices.verifyToken(token);
            securityContextHolder.setContext(context);
            chain.doFilter(request, response);
        } catch (JWTVerificationException ex) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}