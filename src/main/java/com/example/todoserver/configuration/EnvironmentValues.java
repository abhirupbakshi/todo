package com.example.todoserver.configuration;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

/**
 * This class centralizes all the directly used environment variable values in one place.
 */
@Component
public class EnvironmentValues {

    public final List<String> TODO_CORS_ALLOWED_ORIGINS;
    public final List<String> TODO_CORS_ALLOWED_METHODS;
    public final List<String> TODO_CORS_ALLOWED_HEADERS;
    public final long TODO_JWT_EXPIRATION_TIME_IN_SECONDS;
    public final List<String> TODO_USER_ROLES;
    public final Key TODO_JWT_SIGNING_KEY;

    @Autowired
    public EnvironmentValues(Environment environment) {
        TODO_CORS_ALLOWED_ORIGINS = Arrays.asList(environment.getProperty("TODO_CORS_ALLOWED_ORIGINS").split(":"));
        TODO_CORS_ALLOWED_METHODS = Arrays.asList(environment.getProperty("TODO_CORS_ALLOWED_METHODS").split(":"));
        TODO_CORS_ALLOWED_HEADERS = Arrays.asList(environment.getProperty("TODO_CORS_ALLOWED_HEADERS").split(":"));
        TODO_JWT_EXPIRATION_TIME_IN_SECONDS = Long.parseLong(environment.getProperty("TODO_JWT_EXPIRATION_TIME_IN_SECONDS"));
        TODO_USER_ROLES = Arrays.asList(environment.getProperty("TODO_USER_ROLES").split(":"));
        TODO_JWT_SIGNING_KEY = Keys.hmacShaKeyFor(environment.getProperty("TODO_JWT_SIGNING_KEY").getBytes());
    }
}
