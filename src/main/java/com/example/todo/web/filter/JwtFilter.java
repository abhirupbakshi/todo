package com.example.todo.web.filter;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.utility.JwtUtilities;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A JWT filter that sets the security context with an authenticated user if the JWT is valid. It also
 * sets a request attribute with the user's username and JWT with the attribute name of {@link ConstantValues.Jwt}'s REQUEST_ATTRIBUTE_TOKEN_KEY.
 * This filter will not run for the requests listed in shouldNotExecuteRequests {@link List}.
 * <br>
 * <br>
 * If the user is unauthenticated, the {@link HttpStatusEntryPoint} will be called with HttpStatus.UNAUTHORIZED.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private JwtUtilities jwtUtilities;
    private final List<RequestMatcher> shouldNotExecuteRequests = List.of(
                    new AntPathRequestMatcher(ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/auth/login", "POST"),
                    new AntPathRequestMatcher(ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/users", "POST")
    );
    private HttpStatusEntryPoint unauthorizedEntryPoint;

    @Autowired
    public void setJwtUtilities(JwtUtilities jwtUtilities) {
        this.jwtUtilities = jwtUtilities;
    }

    @Autowired
    public void setUnauthorizedEntryPoint(HttpStatusEntryPoint unauthorizedEntryPoint) {
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.debug("Parameter:: request: {}, response: {}, filterChain: {}", request, response, filterChain);

        try {

            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            logger.debug("Extracted {} header: {}", HttpHeaders.AUTHORIZATION, authHeader);

            if (authHeader == null) {
                throw new IllegalArgumentException(ConstantValues.RestApi.Error.AUTHORIZATION_HEADER_NOT_FOUND);
            }
            else if (!authHeader.startsWith(ConstantValues.Jwt.BEARER_TOKEN_PREFIX)) {
                throw new IllegalArgumentException(ConstantValues.RestApi.Error.AUTHORIZATION_HEADER_INVALID);
            }

            String jwt = authHeader.substring(ConstantValues.Jwt.BEARER_TOKEN_PREFIX.length() + 1);
            logger.debug("Extracted JWT: {}", jwt);

            UserDetails user = jwtUtilities.getUser(jwt);
            logger.debug("Extracted user details: {}", user);

            Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(user.getUsername(), jwt, user.getAuthorities());
            logger.debug("Created authenticated Authentication object from UserDetails: {}", user);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            logger.debug("Created empty SecurityContext");

            context.setAuthentication(authenticated);
            logger.debug("Authentication object is set to SecurityContext");

            SecurityContextHolder.setContext(context);
            logger.debug("SecurityContext is set to SecurityContextHolder");

            request.setAttribute(ConstantValues.Jwt.REQUEST_ATTRIBUTE_TOKEN_KEY, Map.entry(user.getUsername(), jwt));
            logger.debug("Added request attribute {}:{}", ConstantValues.Jwt.REQUEST_ATTRIBUTE_TOKEN_KEY, Map.entry(user.getUsername(), jwt));

            logger.info("JWT: {} validated successfully", jwt);
        }
        catch (RuntimeException e) {

            logger.warn("Exception in validating JWT: {}", e.getMessage());
            logger.info("Commencing unauthorized entry point as the JWT is invalid or not present");

            unauthorizedEntryPoint.commence(request, response, new AuthenticationException(e.getMessage(), e) {});

            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        logger.debug("Parameter:: request: {}", request);

        boolean match = shouldNotExecuteRequests.stream().anyMatch(matcher -> matcher.matches(request));
        logger.debug("ShouldNotFilter result: {}", match);

        return match;
    }

    public void blacklistJwtInRequestAttribute(HttpServletRequest request) {

        logger.debug("Parameter:: request: {}", request);

        if (request == null) throw new RuntimeException("Request cannot be null");
        logger.debug("Not null check passed for request");

        Object attribute = request.getAttribute(ConstantValues.Jwt.REQUEST_ATTRIBUTE_TOKEN_KEY);
        logger.debug("Extracted request attribute {} = {}", ConstantValues.Jwt.REQUEST_ATTRIBUTE_TOKEN_KEY, attribute);

        if (attribute instanceof Map.Entry entry && entry.getKey() instanceof String username && entry.getValue() instanceof String token) {
            jwtUtilities.blackListJwt(username, token);
            logger.info("Token {} blacklisted for username {}", token, username);
        }
        else
            logger.info("No token was blacklisted");
    }
}
