package com.fijosilo.ecommerce.authentication;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private TestRestTemplate testRestTemplate = new TestRestTemplate();

    private String firstName = "First";
    private String lastName = "Last";
    private String email = "some@email.com";
    private String password = "P455w0rd!";

    @Test
    @Order(1)
    void register() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("firstName", firstName);
        body.add("lastName", lastName);
        body.add("email", email);
        body.add("password", password);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/register",
                HttpMethod.POST,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Failed to register.");
    }

    @Test
    @Order(2)
    void login() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("email", email);
        body.add("password", password);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/login",
                HttpMethod.POST,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to login.");
    }

}
