package com.example.todoserver.web.controller;

import com.example.todoserver.configuration.ConstantValues;
import com.example.todoserver.model.User;
import com.example.todoserver.service.UserService;
import com.example.todoserver.utility.JwtUtilities;
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

        User user = userService.findUser(principal.getName());
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(principal.getName())
                .password("")
                .roles(user.getRoles().toArray(new String[0]))
                .build();
        String jwt = jwtUtilities.createJwt(userDetails);
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.AUTHORIZATION, ConstantValues.Jwt.BEARER_TOKEN_PREFIX + " " + jwt);

        return new ResponseEntity<>(user, headers, HttpStatus.OK);
    }
}
