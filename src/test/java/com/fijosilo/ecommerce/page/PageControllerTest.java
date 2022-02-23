package com.fijosilo.ecommerce.page;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
class PageControllerTest {
    private static PageController pageController;
    private static Page page;

    @BeforeAll
    static void init() {
        page = new Page();
        page.setId(1L);
        page.setTitle("Exmaple page title");
        page.setContent("""
                [h1]Exmaple page title[/h1]

                [p]Some text[/p]""");

        PageService pageService = Mockito.mock(PageService.class);
        Mockito.when(pageService.readPageByTitle(Mockito.anyString())).thenReturn(null);
        Mockito.when(pageService.readPageByTitle(Mockito.matches(page.getTitle()))).thenReturn(page);
        Mockito.when(pageService.createPage(Mockito.any(Page.class))).thenReturn(true);
        Mockito.when(pageService.updatePage(Mockito.any(Page.class))).thenReturn(true);
        Mockito.when(pageService.deletePage(Mockito.any(Page.class))).thenReturn(true);

        pageController = new PageController(pageService);
    }



    @Test
    void readPageMethod_titleIsNotBlankTest() {
        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.readPage("");

        // tests
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("This page was not found.", response.getBody().get("error"));
    }

    @Test
    void readPageMethod_titleIsValidTest() {
        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.readPage("INVALID");

        // tests
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("This page was not found.", response.getBody().get("error"));
    }

    @Test
    void readPageMethod_allParametersAreValidTest() {
        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.readPage(page.getTitle());

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("page"));
        assertEquals(page.getContent(), response.getBody().get("page"));
    }



    @Test
    void createPageMethod_titleIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.createPage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title is required.", response.getBody().get("error"));
    }

    @Test
    void createPageMethod_titleIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.createPage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createPageMethod_titleDoesNotExistTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", page.getTitle());

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.createPage(params);

        // tests
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title can't be the title of an already existing page.", response.getBody().get("error"));
    }

    @Test
    void createPageMethod_contentIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "New Title");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.createPage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field content is required.", response.getBody().get("error"));
    }

    @Test
    void createPageMethod_contentIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "New Title");
        params.put("content", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.createPage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field content can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createPageMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "New Title");
        params.put("content", """
                [h1]New Title[/h1]

                [p]Some text[/p]""");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.createPage(params);

        // tests
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }



    @Test
    void updatePageMethod_titleIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.updatePage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title is required.", response.getBody().get("error"));
    }

    @Test
    void updatePageMethod_titleIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.updatePage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updatePageMethod_titleIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.updatePage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title must be a valid page title.", response.getBody().get("error"));
    }

    @Test
    void updatePageMethod_contentIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", page.getTitle());
        params.put("content", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.updatePage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field content can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updatePageMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", page.getTitle());
        params.put("content", """
                [h1]New Title[/h1]

                [p]Some text[/p]""");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.updatePage(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    void deletePageMethod_titleIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.deletePage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title is required.", response.getBody().get("error"));
    }

    @Test
    void deletePageMethod_titleIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.deletePage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title can't be blank.", response.getBody().get("error"));
    }

    @Test
    void deletePageMethod_titleIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.deletePage(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field title must be a valid page title.", response.getBody().get("error"));
    }

    @Test
    void deletePageMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("title", page.getTitle());

        // response
        ResponseEntity<HashMap<String, Object>> response = pageController.deletePage(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
