package ua.edu.ukma.db.kfc.security;

import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@WebFilter("*")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CorsFilter extends GenericFilter {

    private static final String ALLOWED_HEADERS = String.join(", ",
            List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.USER_AGENT, "Origin",
                    HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.ACCEPT_LANGUAGE,
                    HttpHeaders.CACHE_CONTROL, HttpHeaders.IF_MODIFIED_SINCE, HttpHeaders.LAST_MODIFIED,
                    "X-Remote-IP", "X-Forwarded-List", "X-FORWARDED-FOR", "X-Requested-With")
    );

    private static final String ALLOWED_METHODS= String.join(", ",
            List.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                    HttpMethod.DELETE, HttpMethod.OPTIONS, HttpMethod.HEAD)
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse resp = ((HttpServletResponse) response);
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        resp.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        chain.doFilter(request, response);
    }
}
