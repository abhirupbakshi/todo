package com.example.todoserver.web.filter;

import com.example.todoserver.configuration.Constants;
import com.example.todoserver.utility.JwtUtilities;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A JWT filter that sets the security context with an authenticated user if the JWT is valid. It also
 * sets a request attribute with the user's username and JWT with the attribute name of {@link Constants.Jwt}'s REQUEST_ATTRIBUTE_TOKEN_KEY.
 * This filter will not run for the requests listed in shouldNotExecuteRequests {@link List}.
 * <br>
 * <br>
 * If the user is unauthenticated, the {@link HttpStatusEntryPoint} will be called with HttpStatus.UNAUTHORIZED.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private JwtUtilities jwtUtilities;
    private final List<RequestMatcher> shouldNotExecuteRequests = List.of(
                    new AntPathRequestMatcher(Constants.RestApi.REST_API_ROUTE_PREFIX + "/auth/login", "POST"),
                    new AntPathRequestMatcher(Constants.RestApi.REST_API_ROUTE_PREFIX + "/users", "POST")
            );
    private final AuthenticationEntryPoint unauthorizedEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);

    @Autowired
    public void setJwtUtils(JwtUtilities jwtUtilities) {
        this.jwtUtilities = jwtUtilities;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(Constants.Jwt.BEARER_TOKEN_PREFIX)) {
                throw new RuntimeException("Invalid " + HttpHeaders.AUTHORIZATION + " header");
            }

            String jwt = authHeader.substring(Constants.Jwt.BEARER_TOKEN_PREFIX.length() + 1);
            UserDetails user = jwtUtilities.getUser(jwt);

            Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(user.getUsername(), jwt, user.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authenticated);
            SecurityContextHolder.setContext(context);
            request.setAttribute(Constants.Jwt.REQUEST_ATTRIBUTE_TOKEN_KEY, Map.entry(user.getUsername(), jwt));
        }
        catch (RuntimeException e) {
            unauthorizedEntryPoint.commence(request, response, new AuthenticationException(e.getMessage(), e) {});
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return shouldNotExecuteRequests.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
