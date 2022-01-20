package com.fijosilo.ecommerce.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<HashMap<String, Object>> readClient(Authentication authentication) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }

        payload.put("client", client);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<HashMap<String, Object>> updateClient(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            return new ResponseEntity<>(payload, HttpStatus.UNAUTHORIZED);
        }

        // optional validate firstName
        String firstName = null;
        if (params.containsKey("first_name")) {
            firstName = params.get("first_name").toLowerCase();
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
            if (firstName.isBlank()) {
                payload.put("error", "Field first_name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!firstName.matches("\\p{L}+")) {
                payload.put("error", "Field first_name can contain only letters.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate lastName
        String lastName = null;
        if (params.containsKey("last_name")) {
            lastName = params.get("last_name").toLowerCase();
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
            if (lastName.isBlank()) {
                payload.put("error", "Field last_name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (!lastName.matches("\\p{L}+")) {
                payload.put("error", "Field last_name can contain only letters.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validation tests passed

        // update the address
        if (firstName != null) client.setFirstName(firstName);
        if (lastName != null) client.setLastName(lastName);
        clientService.updateClient(client);

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
