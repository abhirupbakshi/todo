package com.example.todoserver.utility.implementation;

import com.example.todoserver.configuration.ConstantValues;
import com.example.todoserver.configuration.EnvironmentValues;
import com.example.todoserver.utility.JwtUtilities;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link JwtUtilities} interface.
 */
@Component
public class JwtUtilitiesImpl implements JwtUtilities {

    private EnvironmentValues environmentValues;
    private final String delimiter = ":";
    private final Map<String, Set<String>> blacklisted = new HashMap<>();

    @Autowired
    public void setEnvironmentValues(EnvironmentValues environmentValues) {
        this.environmentValues = environmentValues;
    }

    public String createJwt(UserDetails user) {

        String claims = user.getAuthorities() == null ?
                "" :
                user
                        .getAuthorities()
                        .stream()
                        .map(role -> role.getAuthority().split("_")[1])
                        .collect(Collectors.joining(delimiter));

        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + environmentValues.TODO_JWT_EXPIRATION_TIME_IN_SECONDS * 1000))
                .claim("roles", claims)
                .signWith(environmentValues.TODO_JWT_SIGNING_KEY)
                .compact();
    }

    public UserDetails getUser(String token) {

        Jws<Claims> jws = Jwts
                .parserBuilder()
                .setSigningKey(environmentValues.TODO_JWT_SIGNING_KEY)
                .build()
                .parseClaimsJws(token);
        List<String> roles = Arrays
                .stream(jws
                        .getBody()
                        .get("roles", String.class)
                        .split(delimiter))
                .toList();

        UserDetails user = User
                .builder()
                .username(jws.getBody().getSubject())
                .password(token)
                .roles(roles.toArray(new String[0]))
                .build();

        if (blacklisted.containsKey(user.getUsername()) && blacklisted.get(user.getUsername()).contains(token)) {
            throw new JwtException(ConstantValues.Jwt.Error.INVALID_TOKEN);
        }

        return user;
    }

    @Override
    public void blackListJwt(String username, String token) {

        if (blacklisted.containsKey(username))
            blacklisted.get(username).add(token);
        else
            blacklisted.put(username, new HashSet<>(Set.of(token)));
    }
}
