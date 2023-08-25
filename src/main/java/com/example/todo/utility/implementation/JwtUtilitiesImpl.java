package com.example.todo.utility.implementation;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.configuration.EnvironmentValues;
import com.example.todo.utility.JwtUtilities;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link JwtUtilities} interface.
 */
@Component
public class JwtUtilitiesImpl implements JwtUtilities {

    private final Logger logger = LoggerFactory.getLogger(JwtUtilitiesImpl.class);
    private EnvironmentValues environmentValues;
    private final String delimiter = ":";
    private final Map<String, Set<String>> blacklisted = new HashMap<>();

    @Autowired
    public void setEnvironmentValues(EnvironmentValues environmentValues) {
        this.environmentValues = environmentValues;
    }

    public String createJwt(UserDetails user) {

        logger.debug("Parameters:: user: {}", user);

        if (user == null) throw new RuntimeException("User cannot be null");
        logger.debug("Not null check passed for UserDetails");

        String authorities = user.getAuthorities() == null ?
                "" :
                user
                        .getAuthorities()
                        .stream()
                        .map(role -> role.getAuthority().split("_")[1])
                        .collect(Collectors.joining(delimiter));
        logger.debug("Generated authorities: {} from UserDetails authorities: {}", authorities, user.getAuthorities());

        String jwt = Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + environmentValues.TODO_JWT_EXPIRATION_TIME_IN_SECONDS * 1000))
                .claim("roles", authorities)
                .signWith(environmentValues.TODO_JWT_SIGNING_KEY)
                .compact();
        logger.info("Generated JWT: {} for UserDetails: {}", jwt, user);

        return jwt;
    }

    @Override
    public void blackListJwt(String username, String token) {

        logger.debug("Parameters:: username: {}, token: {}", username, token);

        if (blacklisted.containsKey(username)) {
            blacklisted.get(username).add(token);
            logger.debug("User is present in blacklisted map. Added token: {} for username: {}", token, username);
        }
        else {
            blacklisted.put(username, new HashSet<>(Set.of(token)));
            logger.debug("User is absent in blacklisted map. Added new entry with username: {} and token: {}", username, token);
        }

        logger.info("Blacklisting of token: {} for username: {} is successful", token, username);
    }

    public UserDetails getUser(String token) {

        logger.debug("Parameters:: token: {}", token);

        if (token == null) throw new RuntimeException("Token cannot be null");
        logger.debug("Not null check passed for token");

        Jws<Claims> jws = Jwts
                .parserBuilder()
                .setSigningKey(environmentValues.TODO_JWT_SIGNING_KEY)
                .build()
                .parseClaimsJws(token);
        logger.debug("Parsed JWS: {}", jws);

        List<String> roles = Arrays
                .stream(jws
                        .getBody()
                        .get("roles", String.class)
                        .split(delimiter))
                .toList();
        logger.debug("Parsed roles/authorities: {}", roles);

        UserDetails user = User
                .builder()
                .username(jws.getBody().getSubject())
                .password(token)
                .roles(roles.toArray(new String[0]))
                .build();
        logger.info("Parsed User detail: {}", user);

        if (blacklisted.containsKey(user.getUsername()) && blacklisted.get(user.getUsername()).contains(token)) {
            logger.debug("Token is present in blacklisted map");
            throw new JwtException(ConstantValues.Jwt.Error.INVALID_TOKEN);
        }

        return user;
    }
}
