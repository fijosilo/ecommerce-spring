package com.fijosilo.ecommerce.product;

import com.fijosilo.ecommerce.category.Category;
import com.fijosilo.ecommerce.category.CategoryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductControllerTest {
    private static ProductController productController;
    private static Product productPhone, productTable;
    private static List<Product> products, productsNovelties, productsPromotions;

    @BeforeAll
    static void init() {
        // category service

        Category categoryTechnology = new Category();
        categoryTechnology.setId(1L);
        categoryTechnology.setName("TECHNOLOGY");
        categoryTechnology.setParent(null);
        categoryTechnology.setEnabled(true);

        CategoryService categoryService = Mockito.mock(CategoryService.class);
        Mockito.when(categoryService.readCategoryByName(Mockito.anyString())).thenReturn(null);
        Mockito.when(categoryService.readCategoryByName(Mockito.matches(categoryTechnology.getName()))).thenReturn(categoryTechnology);
        Mockito.when(categoryService.createCategory(Mockito.any(Category.class))).thenReturn(true);

        // product service

        ProductBrand brandQuasar = new ProductBrand();
        brandQuasar.setId(1L);
        brandQuasar.setBrand("QUASAR");

        productTable = new Product();
        productTable.setId(1L);
        productTable.setCode("QT1642517235938");
        productTable.setProductBrand(brandQuasar);
        productTable.setName("Quasar Table");
        productTable.setDescription("A fictitious table for testing purposes");
        productTable.setPrice(499.99);
        productTable.setStock(10);
        productTable.setThumbnailURL(null);
        productTable.setDiscount(0.1);
        productTable.setImagesURL(null);
        productTable.setAdditionDate(1642517236929L);
        productTable.setEnabled(true);

        productPhone = new Product();
        productPhone.setId(2L);
        productPhone.setCode("QS1642517236929");
        productPhone.setProductBrand(brandQuasar);
        productPhone.setName("Quasar Smartphone");
        productPhone.setDescription("A fictitious smartphone for testing purposes");
        productPhone.setPrice(199.99);
        productPhone.setStock(10);
        productPhone.setThumbnailURL(null);
        productPhone.setDiscount(0.2);
        productPhone.setImagesURL(null);
        productPhone.addProductCategory(categoryTechnology);
        productPhone.setAdditionDate(1642517236929L);
        productPhone.setEnabled(true);

        products = new LinkedList<>();
        products.add(productTable);
        products.add(productPhone);

        productsNovelties = new LinkedList<>();
        productsNovelties.add(productPhone);
        productsNovelties.add(productTable);

        productsPromotions = new LinkedList<>();
        productsPromotions.add(productPhone);
        productsPromotions.add(productTable);

        ProductService productService = Mockito.mock(ProductService.class);
        Mockito.when(productService.readProductByCode(Mockito.anyString())).thenReturn(null);
        Mockito.when(productService.readProductByCode(Mockito.matches(productPhone.getCode()))).thenReturn(productPhone);
        Mockito.when(productService.readProductByCode(Mockito.matches(productTable.getCode()))).thenReturn(productTable);
        Mockito.when(productService.readProductsByFilters(Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(products);
        Mockito.when(productService.readProductsByFilters(Mockito.anyString(),
                Mockito.anyDouble(),
                Mockito.anyDouble(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(products);
        Mockito.when(productService.readProductBrandByBrand(Mockito.anyString())).thenReturn(null);
        Mockito.when(productService.readProductBrandByBrand(Mockito.matches(brandQuasar.getBrand()))).thenReturn(brandQuasar);
        Mockito.when(productService.createProductBrand(Mockito.any(ProductBrand.class))).thenReturn(true);
        Mockito.when(productService.createProduct(Mockito.any(Product.class))).thenReturn(true);
        Mockito.when(productService.updateProduct(Mockito.any(Product.class))).thenReturn(true);
        Mockito.when(productService.deleteProduct(Mockito.any(Product.class))).thenReturn(true);
        Mockito.when(productService.readProductsByDescendingDate(Mockito.anyInt(), Mockito.anyInt())).thenReturn(productsNovelties);
        Mockito.when(productService.readProductsByDescendingDiscount(Mockito.anyInt(), Mockito.anyInt())).thenReturn(productsPromotions);

        // product controller
        productController = new ProductController(productService, categoryService);
    }



    @Test
    void readProductMethod_codeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void readProductMethod_codeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductMethod_codeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must be a valid product code.", response.getBody().get("error"));
    }

    @Test
    void readProductMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProduct(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("product"));
        assertNotNull(response.getBody().get("product"));
        assertTrue(response.getBody().get("product") instanceof Product);
        assertEquals(productPhone, response.getBody().get("product"));
    }



    @Test
    void readProductsMethod_nameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_minPriceIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("min_price", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field min_price can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_minPriceIsValidRationalTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2"};
        for (String s : invalidPrices) {
            params.put("min_price", s);

            response = productController.readProducts(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field min_price must be a valid rational number.", response.getBody().get("error"));
        }
    }

    @Test
    void readProductsMethod_minPriceIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("min_price", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field min_price can't be negative.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_maxPriceIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_price", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_price can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_maxPriceIsValidRationalTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2"};
        for (String s : invalidPrices) {
            params.put("max_price", s);

            response = productController.readProducts(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field max_price must be a valid rational number.", response.getBody().get("error"));
        }
    }

    @Test
    void readProductsMethod_maxPriceIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_price", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_price can't be negative.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_maxPriceIsBiggerThanMinPriceTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("min_price", "3");
        params.put("max_price", "2");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field min_price can't be bigger than max_price.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_brandIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field brand can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_categoriesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("categories[0]", "WHITE");
        params.put("categories[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field categories[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_maxProductsPerPageIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_maxProductsPerPageIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_maxProductsPerPageIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_pageNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_pageNumberIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_pageNumberIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readProductsMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readProducts(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("products"));
        assertNotNull(response.getBody().get("products"));
        assertTrue(response.getBody().get("products") instanceof List);
        assertEquals(products, response.getBody().get("products"));
    }



    @Test
    void createProductMethod_brandIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field brand is required.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_brandIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field brand can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_nameIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field name is required.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_nameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_descriptionIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field description is required.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_descriptionIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field description can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_priceIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field price is required.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_priceIsValidRationalTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2"};
        for (String s : invalidPrices) {
            params.put("price", s);

            response = productController.createProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field price must be a valid rational number.", response.getBody().get("error"));
        }
    }

    @Test
    void createProductMethod_priceIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field price can't be negative.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_stockIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field stock is required.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_stockIsValidIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2", "3.5"};
        for (String s : invalidPrices) {
            params.put("stock", s);

            response = productController.createProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field stock must be a valid rational number.", response.getBody().get("error"));
        }
    }

    @Test
    void createProductMethod_stockIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field stock can't be negative.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_thumbnailIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field thumbnail is required.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_thumbnailIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");
        params.put("thumbnail", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field thumbnail can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_discountIsValidIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");
        params.put("thumbnail", "website.com/images/thumbnail.jpg");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2", "3.5"};
        for (String s : invalidPrices) {
            params.put("discount", s);

            response = productController.createProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field discount must be a valid integer number.", response.getBody().get("error"));
        }
    }

    @Test
    void createProductMethod_discountIsInValidRangeTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");
        params.put("thumbnail", "website.com/images/thumbnail.jpg");

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"-1", "101"};
        for (String s : invalidPrices) {
            params.put("discount", s);

            response = productController.createProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field discount must be between 0 and 100.", response.getBody().get("error"));
        }
    }

    @Test
    void createProductMethod_imagesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");
        params.put("thumbnail", "website.com/images/thumbnail.jpg");
        params.put("images[0]", "website.com/images/image.jpg");
        params.put("images[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field images[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_categoriesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");
        params.put("thumbnail", "website.com/images/thumbnail.jpg");
        params.put("categories[0]", "FURNITURE");
        params.put("categories[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field categories[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void createProductMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");
        params.put("thumbnail", "website.com/images/thumbnail.jpg");
        params.put("enabled", "true");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.createProduct(params);

        // tests
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }



    @Test
    void updateProductMethod_codeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_codeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_codeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must contain a valid product code.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_brandIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("brand", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field brand can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_nameIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("name", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field name can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_descriptionIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("description", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field description can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_priceIsValidRationalTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2"};
        for (String s : invalidPrices) {
            params.put("price", s);

            response = productController.updateProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field price must be a valid rational number.", response.getBody().get("error"));
        }
    }

    @Test
    void updateProductMethod_priceIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("price", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field price can't be negative.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_stockIsValidIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2", "3.5"};
        for (String s : invalidPrices) {
            params.put("stock", s);

            response = productController.updateProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field stock must be a valid rational number.", response.getBody().get("error"));
        }
    }

    @Test
    void updateProductMethod_stockIsPositiveTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("stock", "-3");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field stock can't be negative.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_thumbnailIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("thumbnail", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field thumbnail can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_discountIsValidIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"12a21", "!2", "3.5"};
        for (String s : invalidPrices) {
            params.put("discount", s);

            response = productController.updateProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field discount must be a valid integer number.", response.getBody().get("error"));
        }
    }

    @Test
    void updateProductMethod_discountIsInValidRangeTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response;

        // tests
        String[] invalidPrices = new String[]{"-1", "101"};
        for (String s : invalidPrices) {
            params.put("discount", s);

            response = productController.updateProduct(params);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.getBody().containsKey("error"));
            assertEquals("Field discount must be between 0 and 100.", response.getBody().get("error"));
        }
    }

    @Test
    void updateProductMethod_imagesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("images[0]", "website.com/images/image.jpg");
        params.put("images[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field images[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_categoriesAreNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productPhone.getCode());
        params.put("categories[0]", "FURNITURE");
        params.put("categories[1]", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field categories[1] can't be blank.", response.getBody().get("error"));
    }

    @Test
    void updateProductMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productTable.getCode());
        params.put("brand", "WHITE");
        params.put("name", "Oak Chair");
        params.put("description", "A light oak chair");
        params.put("price", "59.99");
        params.put("stock", "10");
        params.put("thumbnail", "website.com/images/thumbnail.jpg");
        params.put("discount", "20");
        params.put("images[0]", "website.com/images/image1.jpg");
        params.put("images[2]", "website.com/images/image2.jpg");
        params.put("categories[0]", "FURNITURE");
        params.put("enabled", "true");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.updateProduct(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    void deleteProductMethod_codeIsRequiredTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.deleteProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code is required.", response.getBody().get("error"));
    }

    @Test
    void deleteProductMethod_codeIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.deleteProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code can't be blank.", response.getBody().get("error"));
    }

    @Test
    void deleteProductMethod_codeIsValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "INVALID");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.deleteProduct(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field code must contain a valid product code.", response.getBody().get("error"));
    }

    @Test
    void deleteProductMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("code", productTable.getCode());

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.deleteProduct(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



    @Test
    void readNoveltiesMethod_maxProductsPerPageIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readNovelties(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readNoveltiesMethod_maxProductsPerPageIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readNovelties(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readNoveltiesMethod_maxProductsPerPageIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readNovelties(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readNoveltiesMethod_pageNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readNovelties(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readNoveltiesMethod_pageNumberIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readNovelties(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readNoveltiesMethod_pageNumberIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readNovelties(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readNoveltiesMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readNovelties(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("products"));
        assertNotNull(response.getBody().get("products"));
        assertTrue(response.getBody().get("products") instanceof List);
        assertEquals(productsNovelties, response.getBody().get("products"));
    }



    @Test
    void readPromotionsMethod_maxProductsPerPageIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readPromotions(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readPromotionsMethod_maxProductsPerPageIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readPromotions(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readPromotionsMethod_maxProductsPerPageIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("max_products_per_page", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readPromotions(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field max_products_per_page can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readPromotionsMethod_pageNumberIsNotBlankTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readPromotions(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be blank.", response.getBody().get("error"));
    }

    @Test
    void readPromotionsMethod_pageNumberIsIntegerTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "9.5");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readPromotions(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number must be a valid integer number.", response.getBody().get("error"));
    }

    @Test
    void readPromotionsMethod_pageNumberIsBiggerThanZeroTest() {
        // request
        HashMap<String, String> params = new HashMap<>();
        params.put("page_number", "0");

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readPromotions(params);

        // tests
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Field page_number can't be smaller than one.", response.getBody().get("error"));
    }

    @Test
    void readPromotionsMethod_allParametersAreValidTest() {
        // request
        HashMap<String, String> params = new HashMap<>();

        // response
        ResponseEntity<HashMap<String, Object>> response = productController.readPromotions(params);

        // tests
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody().containsKey("products"));
        assertNotNull(response.getBody().get("products"));
        assertTrue(response.getBody().get("products") instanceof List);
        assertEquals(productsPromotions, response.getBody().get("products"));
    }

}
