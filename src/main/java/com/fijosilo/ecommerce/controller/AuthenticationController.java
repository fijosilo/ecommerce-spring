package com.fijosilo.ecommerce.controller;

import com.fijosilo.ecommerce.dto.User;
import com.fijosilo.ecommerce.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class AuthenticationController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthenticationController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/register")
    public HashMap<String, Object> register(@RequestParam HashMap<String, Object> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate first name
        if (!params.containsKey("firstName")) {
            response.put("message", "Field first name is required.");
            return response;
        }
        String firstName = params.get("firstName").toString();
        if (firstName.isBlank()) {
            response.put("message", "Field first name can't be blank.");
            return response;
        }
        if (!firstName.matches("\\p{L}+")) {
            response.put("message", "Field first name can contain only letters.");
            return response;
        }
        // validate last name
        if (!params.containsKey("lastName")) {
            response.put("message", "Field last name is required.");
            return response;
        }
        String lastName = params.get("lastName").toString();
        if (lastName.isBlank()) {
            response.put("message", "Field last name can't be blank.");
            return response;
        }
        if (!lastName.matches("\\p{L}+")) {
            response.put("message", "Field last name can contain only letters.");
            return response;
        }
        // validate email
        if (!params.containsKey("email")) {
            response.put("message", "Field email is required.");
            return response;
        }
        String email = params.get("email").toString();
        if (email.isBlank()) {
            response.put("message", "Field email can't be blank.");
            return response;
        }
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            response.put("message", "Field email needs to be a valid email address.");
            return response;
        }
        // validate password
        if (!params.containsKey("password")) {
            response.put("message", "Field password is required.");
            return response;
        }
        String password = params.get("password").toString();
        if (password.isBlank()) {
            response.put("message", "Field password can't be blank.");
            return response;
        }
        if (password.length() < 8) {
            response.put("message", "Field password must have at least 8 characters.");
            return response;
        }
        if (!password.matches(".*\\p{L}.*")) {
            response.put("message", "Field password must contain at least 1 letter.");
            return response;
        }
        if (!password.matches(".*\\p{N}.*")) {
            response.put("message", "Field password must contain at least 1 digit.");
            return response;
        }
        if (password.matches("[\\p{L}\\p{N}\\p{Z}]+")) {
            response.put("message", "Field password must contain at least 1 special character.");
            return response;
        }

        // all validations test passed

        // create the user
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        // encrypt the user password
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("CLIENT");
        user.setEnabled(true);

        // save the user to the database
        if (!userService.createUser(user)) {
            response.put("message", "Database couldn't register the user.");
            return response;
        }

        // user got registered
        response.put("error", false);
        return response;
    }

}
