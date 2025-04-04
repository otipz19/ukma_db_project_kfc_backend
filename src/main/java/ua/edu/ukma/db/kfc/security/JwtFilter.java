package ua.edu.ukma.db.kfc.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        String header = ((HttpServletRequest)request).getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(securityConstants.getTokenPrefix())) {
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = header.replace(securityConstants.getTokenPrefix(), "");
        try {
            SecurityContext context = jwtServices.verifyToken(token);
            securityContextHolder.setContext(context);
            chain.doFilter(request, response);
        } catch (JWTVerificationException ex) {
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}