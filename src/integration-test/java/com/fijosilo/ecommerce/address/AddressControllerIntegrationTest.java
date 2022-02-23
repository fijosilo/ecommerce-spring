package com.fijosilo.ecommerce.address;

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

import static org.junit.jupiter.api.Assertions.*;

@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddressControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Value("${com.fijosilo.ecommerce.admin_email}")
    private String adminEmail;
    @Value("${com.fijosilo.ecommerce.admin_password}")
    private String adminPassword;

    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private String sessionCookie;

    private final String addressPurpose = "CHARGE";
    private final String firstName = "Orange";
    private final String lastName = "Black";
    private final String street = "Color Street";
    private final String number = "3";
    private final String apartment = "1DT";
    private final String postalCode = "1234-123";
    private final String locality = "Colorland";
    private final String country = "Portugal";
    private final String taxNumber = "123456789";
    private final String phoneNumber = "987654321";

    private final String updatedFirstName = "Orange";
    private final String updatedLastName = "Black";

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
    void createAddress() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("address_purpose", addressPurpose);
        body.add("first_name", firstName);
        body.add("last_name", lastName);
        body.add("street", street);
        body.add("number", number);
        body.add("apartment", apartment);
        body.add("postal_code", postalCode);
        body.add("locality", locality);
        body.add("country", country);
        body.add("tax_number", taxNumber);
        body.add("phone_number", phoneNumber);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client/address",
                HttpMethod.POST,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Failed to create an address");
    }

    @Test
    @Order(2)
    void readAddresses() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client/addresses",
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the client addresses");

        assertTrue(response.getBody().containsKey("charge_address"),
                "Response object must contain a property called 'charge_address' with the charge address object.");
        HashMap<String, String> chargeAddressMap = KeyValueMap.fromString(response.getBody().get("charge_address").toString());

        assertTrue(chargeAddressMap.containsKey("firstName"), "'charge_address' must contain a property called 'firstName'.");
        assertEquals(firstName, chargeAddressMap.get("firstName"), "'firstName' property does not match.");
        assertTrue(chargeAddressMap.containsKey("lastName"), "'charge_address' must contain a property called 'lastName'.");
        assertEquals(lastName, chargeAddressMap.get("lastName"), "'lastName' property does not match.");
        assertTrue(chargeAddressMap.containsKey("street"), "'charge_address' must contain a property called 'street'.");
        assertEquals(street, chargeAddressMap.get("street"), "'street' property does not match.");
        assertTrue(chargeAddressMap.containsKey("number"), "'charge_address' must contain a property called 'number'.");
        assertEquals(number, chargeAddressMap.get("number"), "'number' property does not match.");
        assertTrue(chargeAddressMap.containsKey("apartment"), "'apartment' must contain a property called 'firstName'.");
        assertEquals(apartment, chargeAddressMap.get("apartment"), "'apartment' property does not match.");
        assertTrue(chargeAddressMap.containsKey("postalCode"), "'postalCode' must contain a property called 'firstName'.");
        assertEquals(postalCode, chargeAddressMap.get("postalCode"), "'postalCode' property does not match.");
        assertTrue(chargeAddressMap.containsKey("locality"), "'locality' must contain a property called 'firstName'.");
        assertEquals(locality, chargeAddressMap.get("locality"), "'locality' property does not match.");
        assertTrue(chargeAddressMap.containsKey("country"), "'country' must contain a property called 'firstName'.");
        assertEquals(country, chargeAddressMap.get("country"), "'country' property does not match.");
        assertTrue(chargeAddressMap.containsKey("taxNumber"), "'taxNumber' must contain a property called 'firstName'.");
        assertEquals(taxNumber, chargeAddressMap.get("taxNumber"), "'taxNumber' property does not match.");
        assertTrue(chargeAddressMap.containsKey("phoneNumber"), "'phoneNumber' must contain a property called 'firstName'.");
        assertEquals(phoneNumber, chargeAddressMap.get("phoneNumber"), "'phoneNumber' property does not match.");
    }

    @Test
    @Order(3)
    void updateAddress() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("address_purpose", addressPurpose);
        body.add("first_name", updatedFirstName);
        body.add("last_name", updatedLastName);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client/address",
                HttpMethod.PUT,
                request,
                HashMap.class
        );

        // test
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to update the client address");

        // read the updated address
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        body= new LinkedMultiValueMap<>();

        request = new HttpEntity<>(body, headers);

        response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client/addresses",
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // test the updated address
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the updated client address");

        assertTrue(response.getBody().containsKey("charge_address"),
                "Response object must contain a property called 'charge_address' with the charge address object.");
        HashMap<String, String> chargeAddressMap = KeyValueMap.fromString(response.getBody().get("charge_address").toString());

        assertTrue(chargeAddressMap.containsKey("firstName"), "'charge_address' must contain a property called 'firstName'.");
        assertEquals(updatedFirstName, chargeAddressMap.get("firstName"), "'firstName' property does not match.");
        assertTrue(chargeAddressMap.containsKey("lastName"), "'charge_address' must contain a property called 'lastName'.");
        assertEquals(updatedLastName, chargeAddressMap.get("lastName"), "'lastName' property does not match.");
        assertTrue(chargeAddressMap.containsKey("street"), "'charge_address' must contain a property called 'street'.");
        assertEquals(street, chargeAddressMap.get("street"), "'street' property does not match.");
        assertTrue(chargeAddressMap.containsKey("number"), "'charge_address' must contain a property called 'number'.");
        assertEquals(number, chargeAddressMap.get("number"), "'number' property does not match.");
        assertTrue(chargeAddressMap.containsKey("apartment"), "'apartment' must contain a property called 'firstName'.");
        assertEquals(apartment, chargeAddressMap.get("apartment"), "'apartment' property does not match.");
        assertTrue(chargeAddressMap.containsKey("postalCode"), "'postalCode' must contain a property called 'firstName'.");
        assertEquals(postalCode, chargeAddressMap.get("postalCode"), "'postalCode' property does not match.");
        assertTrue(chargeAddressMap.containsKey("locality"), "'locality' must contain a property called 'firstName'.");
        assertEquals(locality, chargeAddressMap.get("locality"), "'locality' property does not match.");
        assertTrue(chargeAddressMap.containsKey("country"), "'country' must contain a property called 'firstName'.");
        assertEquals(country, chargeAddressMap.get("country"), "'country' property does not match.");
        assertTrue(chargeAddressMap.containsKey("taxNumber"), "'taxNumber' must contain a property called 'firstName'.");
        assertEquals(taxNumber, chargeAddressMap.get("taxNumber"), "'taxNumber' property does not match.");
        assertTrue(chargeAddressMap.containsKey("phoneNumber"), "'phoneNumber' must contain a property called 'firstName'.");
        assertEquals(phoneNumber, chargeAddressMap.get("phoneNumber"), "'phoneNumber' property does not match.");
    }

    @Test
    @Order(4)
    void deleteAddress() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("address_purpose", addressPurpose);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client/address",
                HttpMethod.DELETE,
                request,
                HashMap.class
        );

        // test
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to delete the client address");

        // read the deleted address
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        body= new LinkedMultiValueMap<>();

        request = new HttpEntity<>(body, headers);

        response = testRestTemplate.exchange(
                "http://localhost:" + port + "/client/addresses",
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // test the updated address
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the client addresses");

        assertTrue(response.getBody().containsKey("charge_address"),
                "Response object must contain a property called 'charge_address' with the charge address object.");
        assertNull(response.getBody().get("charge_address"),
                "The 'charge_address' property should be null.");
    }

}
