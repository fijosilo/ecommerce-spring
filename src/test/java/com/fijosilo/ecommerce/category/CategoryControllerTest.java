package com.fijosilo.ecommerce.category;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
class CategoryControllerTest {
    private static CategoryController categoryController;
    private static Category technology;
    private static Set<Category> categories;

    @BeforeAll
    static void init() {
        technology = new Category();
        technology.setId(1L);
        technology.setName("TECHNOLOGY");
        technology.setParent(null);
        technology.setEnabled(true);

        Category smartphone = new Category();
        smartphone.setId(2L);
        smartphone.setName("SMARTPHONE");
        smartphone.setParent(technology);
        smartphone.setEnabled(true);

        Category furniture = new Category();
        furniture.setId(3L);
        furniture.setName("FURNITURE");
        furniture.setParent(null);
        furniture.setEnabled(true);

        categories = new HashSet<>();
        categories.add(technology);
        categories.add(furniture);

        CategoryService categoryService = Mockito.mock(CategoryService.class);
        Mockito.when(categoryService.readCategoryByName(Mockito.anyString())).thenReturn(null);
        Mockito.when(categoryService.readCategoryByName(Mockito.matches("TECHNOLOGY"))).thenReturn(technology);
        Mockito.when(categoryService.readCategoryByName(Mockito.matches("SMARTPHONE"))).thenReturn(smartphone);
        Mockito.when(categoryService.readCategoryByName(Mockito.matches("FURNITURE"))).thenReturn(furniture);
        Mockito.when(categoryService.readCategories()).thenReturn(categories);
        Mockito.when(categoryService.createCategory(Mockito.any(Category.class))).thenReturn(true);
        Mockito.when(categoryService.updateCategory(Mockito.any(Category.class))).thenReturn(true);
        Mockito.when(categoryService.deleteCategory(Mockito.any(Category.class))).thenReturn(true);

        categoryController = new CategoryController(categoryService);
    }



    @Test
    void readCategoryMethod_categoryNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.readCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name is required.", response.getBody().get("error"));
    }

    @Test
    void readCategoryMethod_categoryNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.readCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readCategoryMethod_categoryNameIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.readCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name must be a valid category name.", response.getBody().get("error"));
    }

    @Test
    void readCategoryMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "TECHNOLOGY");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.readCategory(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().containsKey("error"));

        assertTrue(response.getBody().containsKey("category"));
        assertNotNull(response.getBody().get("category"));
        assertTrue(response.getBody().get("category") instanceof Category);
        assertEquals(technology, response.getBody().get("category"));
    }



    @Test
    void readCategoriesMethod_Test() {
        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.readCategories();

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("categories"));
        assertNotNull(response.getBody().get("categories"));
        assertTrue(response.getBody().get("categories") instanceof Set);
        assertEquals(categories, response.getBody().get("categories"));
    }




    @Test
    void createCategoryMethod_categoryNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.createCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name is required.", response.getBody().get("error"));
    }

    @Test
    void createCategoryMethod_categoryNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.createCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createCategoryMethod_categoryNameDoesNotExistTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "TECHNOLOGY");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.createCategory(params);

        // tests
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("A category with the provided category_name already exists in the database.", response.getBody().get("error"));
    }

    @Test
    void createCategoryMethod_parentCategoryNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "COMPUTER");
        params.put("category_parent_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.createCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_parent_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createCategoryMethod_parentCategoryNameIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "COMPUTER");
        params.put("category_parent_name", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.createCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_parent_name must be a valid category name.", response.getBody().get("error"));
    }

    @Test
    void createCategoryMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "COMPUTER");
        params.put("category_parent_name", "TECHNOLOGY");
        params.put("enabled", "true");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.createCategory(params);

        // tests
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }




    @Test
    void updateCategoryMethod_categoryNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.updateCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name is required.", response.getBody().get("error"));
    }

    @Test
    void updateCategoryMethod_categoryNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.updateCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateCategoryMethod_categoryNameIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.updateCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name must be a valid category name.", response.getBody().get("error"));
    }

    @Test
    void updateCategoryMethod_newCategoryNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "SMARTPHONE");
        params.put("category_new_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.updateCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_new_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateCategoryMethod_newParentCategoryNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "SMARTPHONE");
        params.put("category_parent_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.updateCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_parent_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateCategoryMethod_newParentCategoryNameIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "SMARTPHONE");
        params.put("category_parent_name", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.updateCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_parent_name must be a valid category name.", response.getBody().get("error"));
    }

    @Test
    void updateCategoryMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "SMARTPHONE");
        params.put("category_parent_name", "null");
        params.put("enabled", "false");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.updateCategory(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }




    @Test
    void deleteCategoryMethod_categoryNameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.deleteCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name is required.", response.getBody().get("error"));
    }

    @Test
    void deleteCategoryMethod_categoryNameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.deleteCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void deleteCategoryMethod_categoryNameIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.deleteCategory(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field category_name must be a valid category name.", response.getBody().get("error"));
    }

    @Test
    void deleteCategoryMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("category_name", "FURNITURE");

        // response
        ResponseEntity<HashMap<String, Object>> response = categoryController.deleteCategory(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
