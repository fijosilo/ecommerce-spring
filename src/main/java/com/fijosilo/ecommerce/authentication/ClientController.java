package com.fijosilo.ecommerce.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public HashMap<String, Object> readClient(Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("error", true);
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // response
        response.put("error", false);
        response.put("client", client);
        return response;
    }

    @PutMapping
    public HashMap<String, Object> updateClient(@RequestParam HashMap<String, String> params, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate client (should never fail unless security configurations are not properly configured)
        Client client = clientService.readClientByEmail(authentication.getName());
        if (client == null) {
            response.put("message", "This endpoint must be accessed while authenticated.");
            return response;
        }

        // optional validate firstName
        String firstName = null;
        if (params.containsKey("first_name")) {
            firstName = params.get("first_name").toLowerCase();
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
            if (firstName.isBlank()) {
                response.put("message", "Field first_name can't be blank.");
                return response;
            }
            if (!firstName.matches("\\p{L}+")) {
                response.put("message", "Field first_name can contain only letters.");
                return response;
            }
        }

        // optional validate lastName
        String lastName = null;
        if (params.containsKey("last_name")) {
            lastName = params.get("last_name").toLowerCase();
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
            if (lastName.isBlank()) {
                response.put("message", "Field last_name can't be blank.");
                return response;
            }
            if (!lastName.matches("\\p{L}+")) {
                response.put("message", "Field last_name can contain only letters.");
                return response;
            }
        }

        // all validation tests passed

        // update the address
        if (firstName != null) client.setFirstName(firstName);
        if (lastName != null) client.setLastName(lastName);
        clientService.updateClient(client);

        //return response
        response.put("error", false);
        return response;
    }

}
