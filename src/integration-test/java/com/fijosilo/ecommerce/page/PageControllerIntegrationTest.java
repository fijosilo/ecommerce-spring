package com.fijosilo.ecommerce.page;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PageControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Value("${com.fijosilo.ecommerce.admin_email}")
    private String adminEmail;
    @Value("${com.fijosilo.ecommerce.admin_password}")
    private String adminPassword;

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();
    private String sessionCookie;

    private final String title = "Title";
    private final String content = """
    [h1]Payment Options[/h1]
    [p]We have the following payment options:[/p]
    """;

    private final String updatedContent = """
    [h1]Payment Options[/h1]
    [p]We have the following payment options:[/p]
    [p]Paypal;[/p]
    [p]Visa.[/p]
    """;

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
    void createPage() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("title", title);
        body.add("content", content);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/page",
                HttpMethod.POST,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Failed to create a page.");
    }

    @Test
    @Order(2)
    void readPage() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/" + title,
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // tests
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the page.");

        assertTrue(response.getBody().containsKey("page"),
                "Response object must contain a property called 'page' with the page content.");
        String pageContent = response.getBody().get("page").toString();

        assertEquals(content, pageContent, "'content' property does not match.");
    }

    @Test
    @Order(3)
    void updatePage() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("title", title);
        body.add("content", updatedContent);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/page",
                HttpMethod.PUT,
                request,
                HashMap.class
        );

        // test
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to update the page.");

        // read the updated category
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        body= new LinkedMultiValueMap<>();

        request = new HttpEntity<>(body, headers);

        response = testRestTemplate.exchange(
                "http://localhost:" + port + "/" + title,
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // test the updated address
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to read the updated page.");

        assertTrue(response.getBody().containsKey("page"),
                "Response object must contain a property called 'page' with the page content.");
        String pageContent = response.getBody().get("page").toString();

        assertEquals(updatedContent, pageContent, "'content' property does not match.");
    }

    @Test
    @Order(4)
    void deletePage() {
        // request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        LinkedMultiValueMap<String, String> body= new LinkedMultiValueMap<>();
        body.add("title", title);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // response
        ResponseEntity<HashMap> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/admin/page",
                HttpMethod.DELETE,
                request,
                HashMap.class
        );

        // test
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to delete the page.");

        // read the deleted category
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        body= new LinkedMultiValueMap<>();

        request = new HttpEntity<>(body, headers);

        response = testRestTemplate.exchange(
                "http://localhost:" + port + "/" + title,
                HttpMethod.GET,
                request,
                HashMap.class
        );

        // test the updated address
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Should not be able to read a deleted page.");
    }

}
