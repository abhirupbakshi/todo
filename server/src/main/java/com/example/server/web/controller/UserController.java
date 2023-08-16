package com.example.server.web.controller;

import com.example.server.configuration.Constants;
import com.example.server.model.PasswordUpdateRequest;
import com.example.server.model.User;
import com.example.server.service.UserService;
import com.example.server.utility.JwtUtilities;
import com.example.server.web.controller.validation.group.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping(Constants.RestApi.REST_API_ROUTE_PREFIX + "/users")
public class UserController {

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

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> findUser(Principal principal) {

        User user = userService.findUser(principal.getName());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@Validated(UserValidationGroup.Create.class) @RequestBody User user) {

        User registered = userService.createUser(user);

        return new ResponseEntity<>(registered, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updatePassword(@Validated(UserValidationGroup.UpdatePassword.class) @RequestBody PasswordUpdateRequest values, Principal principal) {

        User updated = userService.updatePassword(principal.getName(), values.getCurrent().getPassword(), values.getModified().getPassword());

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping(path = "/change-email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateEmail(@Validated(UserValidationGroup.UpdateEmail.class) @RequestBody User user, Principal principal) {

        User updated = userService.updateEmail(principal.getName(), user.getEmail());

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@Validated(UserValidationGroup.Update.class) @RequestBody User user, Principal principal) {

        User updated = userService.updateUser(principal.getName(), user);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> deleteUser(HttpServletRequest request, Principal principal) {

        User deleted = userService.deleteUser(principal.getName());
        Object attribute = request.getAttribute(Constants.Jwt.REQUEST_ATTRIBUTE_TOKEN_KEY);

        if (attribute instanceof Map.Entry entry && entry.getKey() instanceof String username && entry.getValue() instanceof String token) {
            jwtUtilities.blackListJwt(username, token);
        }

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}
