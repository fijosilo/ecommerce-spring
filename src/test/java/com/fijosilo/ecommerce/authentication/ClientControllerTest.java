package com.fijosilo.ecommerce.authentication;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
class ClientControllerTest {
    private static Client client;
    private static ClientController clientController;
    private static Authentication authentication;

    @BeforeAll
    static void init() {
        client = new Client();
        client.setId(1L);
        client.setFirstName("Lorem");
        client.setLastName("Ipsum");
        client.setEmail("loremipsum@email.com");
        client.setRole("CLIENT");
        client.setEnabled(true);

        ClientService clientService = Mockito.mock(ClientService.class);
        Mockito.when(clientService.readClientByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(clientService.readClientByEmail(Mockito.matches("loremipsum@email.com"))).thenReturn(client);
        Mockito.when(clientService.updateClient(Mockito.any(Client.class))).thenReturn(true);

        clientController = new ClientController(clientService);

        authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("loremipsum@email.com");
    }

    @Test
    void readClientMethod_isNotAuthenticatedTest() {
        // response
        ResponseEntity<HashMap<String, Object>> response = clientController.readClient(null);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().containsKey("client"));
    }

    @Test
    void readClientMethod_isAuthenticatedTest() {
        // response
        ResponseEntity<HashMap<String, Object>> response = clientController.readClient(authentication);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("client"));
        assertNotNull(response.getBody().get("client"));
        assertTrue(response.getBody().get("client") instanceof Client);
        assertEquals(client, response.getBody().get("client"));
    }

    @Test
    void updateClientMethod_isNotAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = clientController.updateClient(null, params);

        // tests
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void updateClientMethod_isAuthenticatedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = clientController.updateClient(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateClientMethod_firstNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("first_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = clientController.updateClient(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field first_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateClientMethod_firstNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"0range", "!lack"};
        for (String s : invalidNames) {
            params.put("first_name", s);

            response = clientController.updateClient(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field first_name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void updateClientMethod_lastNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("last_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = clientController.updateClient(authentication, params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field last_name can't be blank.", response.getBody().get("error"));
    }


    @Test
    void updateClientMethod_lastNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"0range", "!lack"};
        for (String s : invalidNames) {
            params.put("last_name", s);

            response = clientController.updateClient(authentication, params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field last_name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void updateClientMethod_isAuthenticatedAndAllParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("first_name", "Orange");
        params.put("last_name", "Black");

        // response
        ResponseEntity<HashMap<String, Object>> response = clientController.updateClient(authentication, params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
