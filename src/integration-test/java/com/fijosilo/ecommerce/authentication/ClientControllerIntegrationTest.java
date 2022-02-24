package com.fijosilo.ecommerce.authentication;

import com.fijosilo.ecommerce.KeyValueMap;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Value("${com.fijosilo.ecommerce.admin_email}")
    private String adminEmail;
    @Value("${com.fijosilo.ecommerce.admin_password}")
    private String adminPassword;

    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private String sessionCookie;

    private String firstName;
    private String lastName;

    @BeforeAll
    void init() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("email", adminEmail);
        body.add("password", adminPassword);

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

        // save session cookie
        assertNotNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE), "Failed to read the session cookie");
        sessionCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    }

    @Test
    @Order(1)
    void readClient() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client",
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the client addresses");

        assertTrue(response.getBody().containsKey("client"),
                "Response object must contain a property called 'client' with the client object.");
        HashMap<String, String> clientMap = KeyValueMap.fromString(response.getBody().get("client").toString());
        assertTrue(clientMap.containsKey("firstName"), "'client' must contain a property called 'firstName'.");
        firstName = clientMap.get("firstName");
        assertTrue(clientMap.containsKey("lastName"), "'client' must contain a property called 'lastName'.");
        lastName = clientMap.get("lastName");
    }

    @Test
    @Order(2)
    void updateClient() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        String updatedFirstName = "Updated" + firstName.toLowerCase();
        String updatedLastName = "Updated" + lastName.toLowerCase();

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("first_name", updatedFirstName);
        body.add("last_name", updatedLastName);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client",
                HttpMethod.PUT,
                request,
                HashMap.class
        );

        // test
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to update the client.");

        // read the updated address
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        body= new LinkedMultiValueMap<>();

        request = new HttpEntity<>(body, headers);

        response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client",
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // test the updated address
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the updated client.");

        assertTrue(response.getBody().containsKey("client"),
                "Response object must contain a property called 'client' with the client object.");
        HashMap<String, String> clientMap = KeyValueMap.fromString(response.getBody().get("client").toString());

        assertTrue(clientMap.containsKey("firstName"), "'client' must contain a property called 'firstName'.");
        assertEquals(updatedFirstName, clientMap.get("firstName"), "'firstName' property does not match.");
        assertTrue(clientMap.containsKey("lastName"), "'client' must contain a property called 'lastName'.");
        assertEquals(updatedLastName, clientMap.get("lastName"), "'lastName' property does not match.");
    }

}
