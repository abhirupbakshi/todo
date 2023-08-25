package com.example.todo.utility;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * An interface that provides helper methods for doing Jwt token related tasks.
 */
public interface JwtUtilities {

    /**
     * Creates a JWT token for the given user details.
     * @param user The user details of type {@link UserDetails}.
     * @return The JWT token.
     */
    String createJwt(UserDetails user);

    /**
     * Extracts the user details from the given JWT token.
     * @param token The JWT token.
     * @return The user details as a {@link UserDetails} object.
     * @throws RuntimeException or it's subclasses if the JWT token is invalid or the user details could not be extracted from it.
     */
    UserDetails getUser(String token);

    /**
     * Given a username and a JWT token issued to that user, it blacklists/invalidates the JWT token.
     * @param username The username.
     * @param token The JWT token.
     */
    void blackListJwt(String username, String token);
}
