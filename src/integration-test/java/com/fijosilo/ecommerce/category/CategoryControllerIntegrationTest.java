package com.fijosilo.ecommerce.category;

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
public class CategoryControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Value("${com.fijosilo.ecommerce.admin_email}")
    private String adminEmail;
    @Value("${com.fijosilo.ecommerce.admin_password}")
    private String adminPassword;

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();
    private String sessionCookie;

    private final String categoryName = "TECHNOLOGY";
    private final String categoryParentName = "null";
    private final String enabled = "false";

    private final String updatedCategoryName = "TECH";
    private final String updateEnabled = "true";

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
    void createCategory() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("category_name", categoryName);
        body.add("enabled", enabled);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/category",
                HttpMethod.POST,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Failed to create a category.");
    }

    @Test
    @Order(2)
    void readCategory() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/category?category_name=" + categoryName,
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the category.");

        assertTrue(response.getBody().containsKey("category"),
                "Response object must contain a property called 'category' with the category object.");
        HashMap<String, String> categoryMap = KeyValueMap.fromString(response.getBody().get("category").toString());

        assertTrue(categoryMap.containsKey("name"), "'category' must contain a property called 'name'.");
        assertEquals(categoryName, categoryMap.get("name"), "'name' property does not match.");
        assertTrue(categoryMap.containsKey("parent"), "'category' must contain a property called 'parent'.");
        assertEquals(categoryParentName, categoryMap.get("parent"), "'parent' property does not match.");
        assertTrue(categoryMap.containsKey("enabled"), "'category' must contain a property called 'enabled'.");
        assertEquals(enabled, categoryMap.get("enabled"), "'enabled' property does not match.");
    }

    @Test
    @Order(3)
    void updateCategory() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("category_name", categoryName);
        body.add("category_new_name", updatedCategoryName);
        body.add("enabled", updateEnabled);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/category",
                HttpMethod.PUT,
                request,
                HashMap.class
        );

        // test
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to update the category.");

        // read the updated category
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        body= new LinkedMultiValueMap<>();

        request = new HttpEntity<>(body, headers);

        response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/category?category_name=" + updatedCategoryName,
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // test the updated address
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the updated category.");

        assertTrue(response.getBody().containsKey("category"),
                "Response object must contain a property called 'category' with the category object.");
        HashMap<String, String> categoryMap = KeyValueMap.fromString(response.getBody().get("category").toString());

        assertTrue(categoryMap.containsKey("name"), "'category' must contain a property called 'name'.");
        assertEquals(updatedCategoryName, categoryMap.get("name"), "'name' property does not match.");
        assertTrue(categoryMap.containsKey("parent"), "'category' must contain a property called 'parent'.");
        assertEquals(categoryParentName, categoryMap.get("parent"), "'parent' property does not match.");
        assertTrue(categoryMap.containsKey("enabled"), "'category' must contain a property called 'enabled'.");
        assertEquals(updateEnabled, categoryMap.get("enabled"), "'enabled' property does not match.");
    }

    @Test
    @Order(4)
    void deleteCategory() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("category_name", updatedCategoryName);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/category",
                HttpMethod.DELETE,
                request,
                HashMap.class
        );

        // test
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to delete the category.");

        // read the deleted category
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        body= new LinkedMultiValueMap<>();

        request = new HttpEntity<>(body, headers);

        response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/category?category_name=" + updatedCategoryName,
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // test the updated address
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the deleted category.");

        assertTrue(response.getBody().containsKey("category"),
                "Response object must contain a property called 'category' with the category object.");
        HashMap<String, String> categoryMap = KeyValueMap.fromString(response.getBody().get("category").toString());

        assertTrue(categoryMap.containsKey("name"), "'category' must contain a property called 'name'.");
        assertEquals(updatedCategoryName, categoryMap.get("name"), "'name' property does not match.");
        assertTrue(categoryMap.containsKey("parent"), "'category' must contain a property called 'parent'.");
        assertEquals(categoryParentName, categoryMap.get("parent"), "'parent' property does not match.");
        assertTrue(categoryMap.containsKey("enabled"), "'category' must contain a property called 'enabled'.");
        assertEquals("false", categoryMap.get("enabled"), "'enabled' property does not match.");
    }

}
