package com.example.todo.web.controller;

import com.example.todo.configuration.ConstantValues;
import com.example.todo.model.PasswordUpdateRequest;
import com.example.todo.model.User;
import com.example.todo.service.UserService;
import com.example.todo.web.filter.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping(ConstantValues.RestApi.REST_API_ROUTE_PREFIX + "/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private JwtFilter jwtFilter;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJwtFilter(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> findUser(Principal principal, HttpServletRequest request) {

        User user = userService.findUser(principal.getName());
        logger.info("User found with username: {} at request url: {}", user.getUsername(), request.getRequestURL());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user, HttpServletRequest request) {

        user = userService.createUser(user);
        logger.info("User created with username: {} at request url: {}", user.getUsername(), request.getRequestURL());

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest, Principal principal, HttpServletRequest request) {

        User user = userService.updatePassword(principal.getName(), passwordUpdateRequest);
        logger.info("User password updated with username: {} at request url: {}", user.getUsername(), request.getRequestURL());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping(path = "/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateEmail(@RequestBody User user, Principal principal, HttpServletRequest request) {

        user = userService.updateEmail(principal.getName(), user);
        logger.info("User email updated with username: {} at request url: {}", user.getUsername(), request.getRequestURL());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@RequestBody User user, Principal principal, HttpServletRequest request) {

        user = userService.updateUser(principal.getName(), user);
        logger.info("User updated with username: {} at request url: {}", user.getUsername(), request.getRequestURL());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> deleteUser(HttpServletRequest request, Principal principal) {

        User deleted = userService.deleteUser(principal.getName());
        logger.info("User deleted with username: {} at request url: {}", deleted.getUsername(), request.getRequestURL());

        jwtFilter.blacklistJwtInRequestAttribute(request);
        logger.info("JWT token blacklisted for username: {} at request url: {}", deleted.getUsername(), request.getRequestURL());

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}
