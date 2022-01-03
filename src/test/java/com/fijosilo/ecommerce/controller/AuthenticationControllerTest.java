package com.fijosilo.ecommerce.controller;

import com.fijosilo.ecommerce.authentication.AuthenticationController;
import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.authentication.ClientService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {
    private static PasswordEncoder passwordEncoder;
    private static ClientService clientService;

    private static AuthenticationController authenticationController;

    private HashMap<String, String> params;
    private HashMap<String, Object> response;

    @BeforeAll
    static void init() {
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("*****");
        clientService = Mockito.mock(ClientService.class);
        Mockito.when(clientService.createClient(Mockito.any(Client.class))).thenReturn(true);
        authenticationController = new AuthenticationController(passwordEncoder, clientService);
    }

    @Test
    void firstNameIsRequired() {
        params = new HashMap<>();
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field first name is required.", response.get("message"), "message does not match");
    }

    @Test
    void firstNameIsNotBlank() {
        params = new HashMap<>();
        params.put("firstName", "");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field first name can't be blank.", response.get("message"), "message does not match");
    }

    @Test
    void firstNameContainsOnlyLetters() {
        params = new HashMap<>();
        params.put("firstName", "Lor3m");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field first name can contain only letters.", response.get("message"), "message does not match");
        params.put("firstName", "Lorem!");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field first name can contain only letters.", response.get("message"), "message does not match");
    }

    @Test
    void lastNameIsRequired() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field last name is required.", response.get("message"), "message does not match");
    }

    @Test
    void lastNameIsNotBlank() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field last name can't be blank.", response.get("message"), "message does not match");
    }

    @Test
    void lastNameContainsOnlyLetters() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ip5um");
        params.put("email", "loremipsim@email.com");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field last name can contain only letters.", response.get("message"), "message does not match");
        params.put("lastName", "Ipsum!");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field last name can contain only letters.", response.get("message"), "message does not match");
    }

    @Test
    void emailIsRequired() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field email is required.", response.get("message"), "message does not match");
    }

    @Test
    void emailIsNotBlank() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field email can't be blank.", response.get("message"), "message does not match");
    }

    @Test
    void emailIsProperlyFormatted() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim.com");
        params.put("password", "l0rem!psum");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field email needs to be a valid email address.", response.get("message"), "message does not match");
        params.put("email", "loremipsim@com");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field email needs to be a valid email address.", response.get("message"), "message does not match");
    }

    @Test
    void passwordIsRequired() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field password is required.", response.get("message"), "message does not match");
    }

    @Test
    void passwordIsNotBlank() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field password can't be blank.", response.get("message"), "message does not match");
    }

    @Test
    void passwordIsBigEnough() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "A123!");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field password must have at least 8 characters.", response.get("message"), "message does not match");
    }

    @Test
    void passwordMustContainLetters() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "1234567!");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field password must contain at least 1 letter.", response.get("message"), "message does not match");
    }

    @Test
    void passwordMustContainDigits() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "ABCDEFG!");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field password must contain at least 1 digit.", response.get("message"), "message does not match");
    }

    @Test
    void passwordMustContainSpecialCharacters() {
        params = new HashMap<>();
        params.put("firstName", "Lorem");
        params.put("lastName", "Ipsum");
        params.put("email", "loremipsim@email.com");
        params.put("password", "A1234567");
        response = authenticationController.register(params);
        assertEquals(true, response.get("error"), "error does not match");
        assertEquals("Field password must contain at least 1 special character.", response.get("message"), "message does not match");
    }

}
