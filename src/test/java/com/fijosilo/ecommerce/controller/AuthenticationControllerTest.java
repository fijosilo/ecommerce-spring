package com.fijosilo.ecommerce.controller;

import com.fijosilo.ecommerce.authentication.AuthenticationController;
import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {
    private static AuthenticationController authenticationController;

    @BeforeAll
    static void init() {
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("*****");
        ClientService clientService = Mockito.mock(ClientService.class);
        Mockito.when(clientService.createClient(Mockito.any(Client.class))).thenReturn(true);
        authenticationController = new AuthenticationController(passwordEncoder, clientService);
    }

    @Test
    void registerMethod_firstNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field first name is required.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_firstNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field first name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_firstNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"Lor3m", "!orem"};
        for (String s : invalidNames) {
            params.put("firstName", s);

            response = authenticationController.register(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field first name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void registerMethod_lastNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field last name is required.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_lastNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field last name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_lastNameContainsOnlyLettersTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidNames = new String[]{"Lor3m", "!orem"};
        for (String s : invalidNames) {
            params.put("lastName", s);

            response = authenticationController.register(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field last name can contain only letters.", response.getBody().get("error"));
        }
    }

    @Test
    void registerMethod_emailIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field email is required.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_emailIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field email can't be blank.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_emailIsProperlyFormattedTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim.com");
        params.put("password", "l0rem!psum");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidEmails = new String[]{"loremipsim.com", "loremipsim@gmail"};
        for (String s : invalidEmails) {
            params.put("email", s);

            response = authenticationController.register(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field email needs to be a valid email address.", response.getBody().get("error"));
        }
    }

    @Test
    void registerMethod_passwordIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field password is required.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_passwordIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field password can't be blank.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_passwordIsBigEnoughTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "A123!");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field password must have at least 8 characters.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_passwordMustContainLetterTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "1234567!");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field password must contain at least 1 letter.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_passwordMustContainDigitTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "ABCDEFG!");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field password must contain at least 1 digit.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_passwordMustContainSpecialCharacterTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "A1234567");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field password must contain at least 1 special character.", response.getBody().get("error"));
    }

    @Test
    void registerMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "A123456!");

        // response
        ResponseEntity<HashMap<String, Object>> response = authenticationController.register(params);

        // tests
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertFalse(response.getBody().containsKey("error"));
    }

}
