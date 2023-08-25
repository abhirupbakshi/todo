package com.example.todo.web.controller;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.model.Role;
import com.example.todo.model.User;
import com.example.todo.service.UserService;
import com.example.todo.utility.JwtUtilities;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping(ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private UserService userService;
    private JwtUtilities jwtUtilities;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJwtUtils(JwtUtilities jwtUtilities) {
        this.jwtUtilities = jwtUtilities;
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> login(Principal principal) {

        Assert.notNull(principal, ConstantValues.User.Error.CREDENTIALS_NEEDED);
        logger.debug("Not null check passed for principal object");

        User user = userService.findUser(principal.getName());
        logger.debug("User found with username: {}", user.getUsername());

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(principal.getName())
                .password("")
                .roles(user.getRoles().stream().map(Role::getName).toArray(String[]::new))
                .build();
        logger.debug("Created UserDetails: {}", userDetails);

        String jwt = jwtUtilities.createJwt(userDetails);
        logger.debug("Created jwt: {} from UserDetails: {}", jwt, userDetails);

        HttpHeaders headers = new HttpHeaders();

        headers.add(ConstantValues.Jwt.RESPONSE_HEADER_TOKEN, jwt);
        logger.debug("Added response header {}:{}", ConstantValues.Jwt.RESPONSE_HEADER_TOKEN, jwt);

        logger.info("Login successful for user with username: {}. Created jwt: {}", principal.getName(), jwt);

        return new ResponseEntity<>(user, headers, HttpStatus.ACCEPTED);
    }
}
