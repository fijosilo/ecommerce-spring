package com.fijosilo.ecommerce.product;

import com.fijosilo.ecommerce.category.Category;
import com.fijosilo.ecommerce.category.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readProduct(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate product
        Product product = productService.readProductByCode(code);
        if (product == null) {
            payload.put("error", "Field code must be a valid product code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        payload.put("product", product);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readProducts(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // optional validate name
        String name = null;
        if (params.containsKey("name")) {
            name = params.get("name");
            if (name.isBlank()) {
                payload.put("error", "Field name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate minimum price
        Double minPrice = null;
        if (params.containsKey("min_price")) {
            String minPriceString = params.get("min_price");
            if (minPriceString.isBlank()) {
                payload.put("error", "Field min_price can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                minPrice = Double.parseDouble(minPriceString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field min_price must be a valid rational number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (minPrice < 0.0) {
                payload.put("error", "Field min_price can't be negative.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate maximum price
        Double maxPrice = null;
        if (params.containsKey("max_price")) {
            String maxPriceString = params.get("max_price");
            if (maxPriceString.isBlank()) {
                payload.put("error", "Field max_price can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                maxPrice = Double.parseDouble(maxPriceString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field max_price must be a valid rational number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (maxPrice < 0.0) {
                payload.put("error", "Field max_price can't be negative.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // validate minimum price in relation to maximum price
        if (minPrice != null && maxPrice == null) {
            maxPrice = Double.MAX_VALUE;
        }
        if (maxPrice != null && minPrice == null) {
            minPrice = 0.0;
        }
        if (minPrice != null && maxPrice != null) {
            if (minPrice > maxPrice) {
                payload.put("error", "Field min_price can't be bigger than max_price.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate brand
        String brand = null;
        if (params.containsKey("brand")) {
            brand = params.get("brand").toUpperCase();
            if (brand.isBlank()) {
                payload.put("error", "Field brand can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate categories
        LinkedList<String> categories = new LinkedList<>();
        int i = 0;
        String key = String.format("categories[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field categories[%d] can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            categories.add(params.get(key).toLowerCase());
            i++;
            key = String.format("categories[%d]", i);
        }
        if (categories.size() == 0) {
            categories = null;
        }
        // optional validate maximum products per page
        Integer maxProductsPerPage = 10;
        if (params.containsKey("max_products_per_page")) {
            String maxProductsPerPageString = params.get("max_products_per_page");
            if (maxProductsPerPageString.isBlank()) {
                payload.put("error", "Field max_products_per_page can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                maxProductsPerPage = Integer.parseInt(maxProductsPerPageString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field max_products_per_page must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (maxProductsPerPage < 1) {
                payload.put("error", "Field max_products_per_page can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate page number
        Integer pageNumber = 1;
        if (params.containsKey("page_number")) {
            String pageNumberString = params.get("page_number");
            if (pageNumberString.isBlank()) {
                payload.put("error", "Field page_number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                pageNumber = Integer.parseInt(pageNumberString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field page_number must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (pageNumber < 1) {
                payload.put("error", "Field page_number can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // get product list
        List<Product> products = productService.readProductsByFilters(name, minPrice, maxPrice, brand, categories, maxProductsPerPage, pageNumber);

        payload.put("products", products);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> createProduct(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate brand
        if (!params.containsKey("brand")) {
            payload.put("error", "Field brand is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String strBrand = params.get("brand").toUpperCase();
        if (strBrand.isBlank()) {
            payload.put("error", "Field brand can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        ProductBrand productBrand = productService.readProductBrandByBrand(strBrand);
        if (productBrand == null) {
            productBrand = new ProductBrand();
            productBrand.setBrand(strBrand);
            if (!productService.createProductBrand(productBrand)) {
                payload.put("error", String.format("Database couldn't register the product brand '%s'.", strBrand));
                return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        // validate name
        if (!params.containsKey("name")) {
            payload.put("error", "Field name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String name = params.get("name");
        if (name.isBlank()) {
            payload.put("error", "Field name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate description
        if (!params.containsKey("description")) {
            payload.put("error", "Field description is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String description = params.get("description");
        if (description.isBlank()) {
            payload.put("error", "Field description can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate price
        if (!params.containsKey("price")) {
            payload.put("error", "Field price is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        double price;
        try {
            price = Double.parseDouble(params.get("price"));
        } catch (NumberFormatException e) {
            payload.put("error", "Field price must be a valid rational number.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (price < 0.0) {
            payload.put("error", "Field price can't be negative.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate stock
        if (!params.containsKey("stock")) {
            payload.put("error", "Field stock is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        int stock;
        try {
            stock = Integer.parseInt(params.get("stock"));
        } catch (NumberFormatException e) {
            payload.put("error", "Field stock must be a valid rational number.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (stock < 0) {
            payload.put("error", "Field stock can't be negative.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // validate thumbnail
        if (!params.containsKey("thumbnail")) {
            payload.put("error", "Field thumbnail is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String thumbnail = params.get("thumbnail");
        if (thumbnail.isBlank()) {
            payload.put("error", "Field thumbnail can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // optional validate discount
        double discount = 0.0;
        if (params.containsKey("discount")) {
            int discountInteger;
            try {
                discountInteger = Integer.parseInt(params.get("discount"));
            } catch (NumberFormatException e) {
                payload.put("error", "Field discount must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (discountInteger < 0 || discountInteger > 100) {
                payload.put("error", "Field discount must be between 0 and 100.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            discount = discountInteger == 0 ? 0.0 : discountInteger / 100.0;
        }

        // optional validate images
        LinkedList<String> images = new LinkedList<>();
        int i = 0;
        String key = String.format("images[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field images[%d] can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            images.add(params.get(key));
            i++;
            key = String.format("images[%d]", i);
        }

        // optional validate categories
        Set<Category> categories = new HashSet<>();
        i = 0;
        key = String.format("categories[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field categories[%d] can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            String categoryName = params.get(key).toLowerCase();
            Category category = categoryService.readCategoryByName(categoryName);
            if (category == null) {
                category = new Category();
                category.setName(categoryName);
                category.setParent(null);
                category.setEnabled(true);
                if (!categoryService.createCategory(category)) {
                    payload.put("error", String.format("Database couldn't register the product category '%s'.", categoryName));
                    return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            categories.add(category);
            i++;
            key = String.format("categories[%d]", i);
        }

        // optional enabled
        boolean enabled = true;
        if (params.containsKey("enabled")) {
            enabled = Boolean.parseBoolean(params.get("enabled"));
        }

        // all validations test passed

        // generate a product code
        String code = name;
        // remove every character of every word except the first one
        code = code.replaceAll("(?!\\b\\S)\\S*", "");
        // remove spaces
        code = code.replaceAll("\\s*", "");
        // capitalize all characters
        code = code.toUpperCase();
        // add epoch
        code = code + System.currentTimeMillis();

        // create the product
        Product product = new Product();
        product.setCode(code);
        product.setProductBrand(productBrand);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setStock(stock);
        product.setThumbnailURL(thumbnail);
        product.setImagesURL(images);
        for (Category c : categories) {
            product.addProductCategory(c);
        }
        product.setAdditionDate(System.currentTimeMillis());
        product.setEnabled(enabled);

        // save the product to the database
        if (!productService.createProduct(product)) {
            payload.put("error", "Database couldn't register the product.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // product got registered
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
    }

    @PutMapping(value = "/admin/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> updateProduct(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate product
        Product product = productService.readProductByCode(code);
        if (product == null) {
            payload.put("error", "Field code must contain a valid product code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // optional validate brand
        ProductBrand productBrand = null;
        if (params.containsKey("brand")) {
            String strBrand = params.get("brand").toUpperCase();
            if (strBrand.isBlank()) {
                payload.put("error", "Field brand can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            productBrand = productService.readProductBrandByBrand(strBrand);
            if (productBrand == null) {
                productBrand = new ProductBrand();
                productBrand.setBrand(strBrand);
                if (!productService.createProductBrand(productBrand)) {
                    payload.put("error", String.format("Database couldn't register the product brand '%s'.", strBrand));
                    return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        // optional validate name
        String name = null;
        if (params.containsKey("name")) {
            name = params.get("name");
            if (name.isBlank()) {
                payload.put("error", "Field name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate description
        String description = null;
        if (params.containsKey("description")) {
            description = params.get("description");
            if (description.isBlank()) {
                payload.put("error", "Field description can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate price
        Double price = null;
        if (params.containsKey("price")) {
            try {
                price = Double.parseDouble(params.get("price"));
            } catch (NumberFormatException e) {
                payload.put("error", "Field price must be a valid rational number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (price < 0.0) {
                payload.put("error", "Field price can't be negative.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate stock
        Integer stock = null;
        if (params.containsKey("stock")) {
            try {
                stock = Integer.parseInt(params.get("stock"));
            } catch (NumberFormatException e) {
                payload.put("error", "Field stock must be a valid rational number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (stock < 0) {
                payload.put("error", "Field stock can't be negative.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate thumbnail
        String thumbnail = null;
        if (params.containsKey("thumbnail")) {
            thumbnail = params.get("thumbnail");
            if (thumbnail.isBlank()) {
                payload.put("error", "Field thumbnail can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        // optional validate discount
        Double discount = null;
        if (params.containsKey("discount")) {
            int discountInteger;
            try {
                discountInteger = Integer.parseInt(params.get("discount"));
            } catch (NumberFormatException e) {
                payload.put("error", "Field discount must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (discountInteger < 0 || discountInteger > 100) {
                payload.put("error", "Field discount must be between 0 and 100.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            discount = discountInteger == 0 ? 0.0 : discountInteger / 100.0;
        }
        // optional validate images
        LinkedList<String> images = new LinkedList<>();
        int i = 0;
        String key = String.format("images[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field images[%d] can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            images.add(params.get(key));
            i++;
            key = String.format("images[%d]", i);
        }
        if (images.size() == 0) {
            images = null;
        }
        // optional validate categories
        Set<Category> categories = new HashSet<>();
        i = 0;
        key = String.format("categories[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                payload.put("error", String.format("Field categories[%d] can't be blank.", i));
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            String categoryName = params.get(key).toLowerCase();
            Category category = categoryService.readCategoryByName(categoryName);
            if (category == null) {
                category = new Category();
                category.setName(categoryName);
                category.setParent(null);
                category.setEnabled(true);
                if (!categoryService.createCategory(category)) {
                    payload.put("error", String.format("Database couldn't register the product category '%s'.", categoryName));
                    return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            categories.add(category);
            i++;
            key = String.format("categories[%d]", i);
        }
        // optional enabled
        Boolean enabled = null;
        if (params.containsKey("enabled")) {
            enabled = Boolean.parseBoolean(params.get("enabled"));
        }

        // all validations test passed

        // update the product
        if (productBrand != null) product.setProductBrand(productBrand);
        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (discount != null) product.setDiscount(discount);
        if (stock != null) product.setStock(stock);
        if (thumbnail != null) product.setThumbnailURL(thumbnail);
        if (images != null) product.setImagesURL(images);
        for (Category c : categories) {
            product.addProductCategory(c);
        }
        if (enabled != null) product.setEnabled(enabled);

        if (!productService.updateProduct(product)) {
            payload.put("error", "Database couldn't update the product.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // product got updated
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @DeleteMapping(value = "/admin/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> deleteProduct(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate code
        if (!params.containsKey("code")) {
            payload.put("error", "Field code is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String code = params.get("code");
        if (code.isBlank()) {
            payload.put("error", "Field code can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate product
        Product product = productService.readProductByCode(code);
        if (product == null) {
            payload.put("error", "Field code must contain a valid product code.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // delete the product
        if (!productService.deleteProduct(product)) {
            payload.put("error", "Database couldn't delete the product.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // product got deleted
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(value = "/products/novelties", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readNovelties(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // optional validate maximum products per page
        Integer maxProductsPerPage = 10;
        if (params.containsKey("max_products_per_page")) {
            String maxProductsPerPageString = params.get("max_products_per_page");
            if (maxProductsPerPageString.isBlank()) {
                payload.put("error", "Field max_products_per_page can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                maxProductsPerPage = Integer.parseInt(maxProductsPerPageString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field max_products_per_page must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (maxProductsPerPage < 1) {
                payload.put("error", "Field max_products_per_page can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate page number
        Integer pageNumber = 1;
        if (params.containsKey("page_number")) {
            String pageNumberString = params.get("page_number");
            if (pageNumberString.isBlank()) {
                payload.put("error", "Field page_number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                pageNumber = Integer.parseInt(pageNumberString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field page_number must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (pageNumber < 1) {
                payload.put("error", "Field page_number can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // get product list
        List<Product> products = productService.readProductsByDescendingDate(maxProductsPerPage, pageNumber);

        payload.put("products", products);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(value = "/products/promotions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readPromotions(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // optional validate maximum products per page
        Integer maxProductsPerPage = 10;
        if (params.containsKey("max_products_per_page")) {
            String maxProductsPerPageString = params.get("max_products_per_page");
            if (maxProductsPerPageString.isBlank()) {
                payload.put("error", "Field max_products_per_page can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                maxProductsPerPage = Integer.parseInt(maxProductsPerPageString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field max_products_per_page must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (maxProductsPerPage < 1) {
                payload.put("error", "Field max_products_per_page can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate page number
        Integer pageNumber = 1;
        if (params.containsKey("page_number")) {
            String pageNumberString = params.get("page_number");
            if (pageNumberString.isBlank()) {
                payload.put("error", "Field page_number can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
                pageNumber = Integer.parseInt(pageNumberString);
            } catch (NumberFormatException e) {
                payload.put("error", "Field page_number must be a valid integer number.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            if (pageNumber < 1) {
                payload.put("error", "Field page_number can't be smaller than one.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // all validations test passed

        // get product list
        List<Product> products = productService.readProductsByDescendingDiscount(maxProductsPerPage, pageNumber);

        payload.put("products", products);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
