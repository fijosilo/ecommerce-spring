package com.fijosilo.ecommerce.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
public class AuthenticationController {
    private final PasswordEncoder passwordEncoder;
    private final ClientService clientService;

    public AuthenticationController(PasswordEncoder passwordEncoder, ClientService clientService) {
        this.passwordEncoder = passwordEncoder;
        this.clientService = clientService;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> register(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate first name
        if (!params.containsKey("firstName")) {
            payload.put("error", "Field first name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String firstName = params.get("firstName");
        if (firstName.isBlank()) {
            payload.put("error", "Field first name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!firstName.matches("\\p{L}+")) {
            payload.put("error", "Field first name can contain only letters.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate last name
        if (!params.containsKey("lastName")) {
            payload.put("error", "Field last name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String lastName = params.get("lastName");
        if (lastName.isBlank()) {
            payload.put("error", "Field last name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!lastName.matches("\\p{L}+")) {
            payload.put("error", "Field last name can contain only letters.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate email
        if (!params.containsKey("email")) {
            payload.put("error", "Field email is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String email = params.get("email");
        if (email.isBlank()) {
            payload.put("error", "Field email can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            payload.put("error", "Field email needs to be a valid email address.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate password
        if (!params.containsKey("password")) {
            payload.put("error", "Field password is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String password = params.get("password");
        if (password.isBlank()) {
            payload.put("error", "Field password can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (password.length() < 8) {
            payload.put("error", "Field password must have at least 8 characters.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!password.matches(".*\\p{L}.*")) {
            payload.put("error", "Field password must contain at least 1 letter.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!password.matches(".*\\p{N}.*")) {
            payload.put("error", "Field password must contain at least 1 digit.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (password.matches("[\\p{L}\\p{N}\\p{Z}]+")) {
            payload.put("error", "Field password must contain at least 1 special character.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // create the user
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        // encrypt the user password
        client.setPassword(passwordEncoder.encode(password));
        client.setRole("CLIENT");
        client.setEnabled(true);

        // save the user to the database
        if (!clientService.createClient(client)) {
            payload.put("error", "Database couldn't register the user.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // user got registered
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
    }

}
