package com.fijosilo.ecommerce.controller;

import com.fijosilo.ecommerce.dto.Product;
import com.fijosilo.ecommerce.dto.ProductBrand;
import com.fijosilo.ecommerce.dto.ProductCategory;
import com.fijosilo.ecommerce.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product")
    public HashMap<String, Object> readProduct(@RequestParam HashMap<String, String> params) {
        // param required code

        HashMap<String, Object> response = new HashMap<>();
        return response;
    }

    @GetMapping("/products")
    public HashMap<String, Object> readProducts(@RequestParam HashMap<String, String> params) {
        /*
         * receive optional params to filter our product
         *
         * name
         * price
         *
         * //need to add
         * brand
         * category
         *
         *
         */

        HashMap<String, Object> response = new HashMap<>();
        return response;
    }

    @PostMapping("/admin/product")
    public HashMap<String, Object> addProduct(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate brand
        if (!params.containsKey("brand")) {
            response.put("message", "Field brand is required.");
            return response;
        }
        String brand = params.get("brand").toUpperCase();
        if (brand.isBlank()) {
            response.put("message", "Field brand can't be blank.");
            return response;
        }
        // validate name
        if (!params.containsKey("name")) {
            response.put("message", "Field name is required.");
            return response;
        }
        String name = params.get("name");
        if (name.isBlank()) {
            response.put("message", "Field name can't be blank.");
            return response;
        }
        // validate description
        if (!params.containsKey("description")) {
            response.put("message", "Field description is required.");
            return response;
        }
        String description = params.get("description");
        if (description.isBlank()) {
            response.put("message", "Field description can't be blank.");
            return response;
        }
        // validate price
        if (!params.containsKey("price")) {
            response.put("message", "Field price is required.");
            return response;
        }
        double price;
        try {
            price = Double.parseDouble(params.get("price"));
        } catch (NumberFormatException e) {
            response.put("message", "Field price is not a valid rational number.");
            return response;
        }
        if (price < 0.0) {
            response.put("message", "Field price can't be negative.");
            return response;
        }
        // validate stock
        if (!params.containsKey("stock")) {
            response.put("message", "Field stock is required.");
            return response;
        }
        int stock;
        try {
            stock = Integer.parseInt(params.get("stock"));
        } catch (NumberFormatException e) {
            response.put("message", "Field stock is not a valid integer number.");
            return response;
        }
        if (stock < 0) {
            response.put("message", "Field stock can't be negative.");
            return response;
        }
        // validate thumbnail
        if (!params.containsKey("thumbnail")) {
            response.put("message", "Field thumbnail is required.");
            return response;
        }
        String thumbnail = params.get("thumbnail");
        if (thumbnail.isBlank()) {
            response.put("message", "Field thumbnail can't be blank.");
            return response;
        }

        // optional validate discount
        double discount = 0.0;
        if (params.containsKey("discount")) {
            try {
                discount = Double.parseDouble(params.get("discount"));
            } catch (NumberFormatException e) {
                response.put("message", "Field discount is not a valid rational number.");
                return response;
            }
            if (discount < 0.0 || discount > 1.0) {
                response.put("message", "Field discount must be between 0.0 and 1.0.");
                return response;
            }
        }
        // optional validate images
        LinkedList<String> images = new LinkedList<>();
        int i = 0;
        String key = String.format("images[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                response.put("message", String.format("Field images[%d] can't be blank.", i));
                return response;
            }
            images.add(params.get(key));
            i++;
            key = String.format("images[%d]", i);
        }
        // optional validate categories
        Set<String> categories = new HashSet<>();
        i = 0;
        key = String.format("categories[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                response.put("message", String.format("Field categories[%d] can't be blank.", i));
                return response;
            }
            categories.add(params.get(key).toLowerCase());
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

        // read or create the product brand
        ProductBrand productBrand = productService.readProductBrandByBrand(brand);
        if (productBrand == null) {
            productBrand = new ProductBrand();
            productBrand.setBrand(brand);
            if (!productService.createProductBrand(productBrand)) {
                response.put("message", "Database couldn't register the product brand.");
                return response;
            }
        }

        // read or create the product categories
        Set<ProductCategory> productCategories = new HashSet<>();
        for (String c : categories) {
            ProductCategory productCategory = productService.readProductCategoryByCategory(c);
            if (productCategory == null) {
                productCategory = new ProductCategory();
                productCategory.setCategory(c);
                if (!productService.createProductCategory(productCategory)) {
                    response.put("message", "Database couldn't register the product category.");
                    return response;
                }
            }
        }

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
        for (ProductCategory pc : productCategories) {
            product.addProductCategory(pc);
            pc.addProduct(product);
        }
        product.setEnabled(enabled);

        // save the product to the database
        if (!productService.createProduct(product)) {
            response.put("message", "Database couldn't register the product.");
            return response;
        }

        // product got registered
        response.put("error", false);
        return response;
    }

    @PutMapping("/admin/product")
    public HashMap<String, Object> updateProduct(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate code
        if (!params.containsKey("code")) {
            response.put("message", "Field code is required.");
            return response;
        }
        String code = params.get("code");
        if (code.isBlank()) {
            response.put("message", "Field code can't be blank.");
            return response;
        }
        Product product = productService.readProductByCode(code);
        if (product == null) {
            response.put("message", "Field code must contain a valid product code.");
            return response;
        }
        // optional validate brand
        String brand = null;
        if (params.containsKey("brand")) {
            brand = params.get("brand").toUpperCase();
            if (brand.isBlank()) {
                response.put("message", "Field brand can't be blank.");
                return response;
            }
        }
        // optional validate name
        String name = null;
        if (params.containsKey("name")) {
            name = params.get("name");
            if (name.isBlank()) {
                response.put("message", "Field name can't be blank.");
                return response;
            }
        }
        // optional validate description
        String description = null;
        if (params.containsKey("description")) {
            description = params.get("description");
            if (description.isBlank()) {
                response.put("message", "Field description can't be blank.");
                return response;
            }
        }
        // optional validate price
        Double price = null;
        if (params.containsKey("price")) {
            try {
                price = Double.parseDouble(params.get("price"));
            } catch (NumberFormatException e) {
                response.put("message", "Field price is not a valid rational number.");
                return response;
            }
            if (price < 0.0) {
                response.put("message", "Field price can't be negative.");
                return response;
            }
        }
        // optional validate stock
        Integer stock = null;
        if (params.containsKey("stock")) {
            try {
                stock = Integer.parseInt(params.get("stock"));
            } catch (NumberFormatException e) {
                response.put("message", "Field stock is not a valid integer number.");
                return response;
            }
            if (stock < 0) {
                response.put("message", "Field stock can't be negative.");
                return response;
            }
        }
        // optional validate thumbnail
        String thumbnail = null;
        if (params.containsKey("thumbnail")) {
            thumbnail = params.get("thumbnail");
            if (thumbnail.isBlank()) {
                response.put("message", "Field thumbnail can't be blank.");
                return response;
            }
        }
        // optional validate discount
        Double discount = null;
        if (params.containsKey("discount")) {
            try {
                discount = Double.parseDouble(params.get("discount"));
            } catch (NumberFormatException e) {
                response.put("message", "Field discount is not a valid rational number.");
                return response;
            }
            if (discount < 0.0 || discount > 1.0) {
                response.put("message", "Field discount must be between 0.0 and 1.0.");
                return response;
            }
        }
        // optional validate images
        LinkedList<String> images = new LinkedList<>();
        int i = 0;
        String key = String.format("images[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                response.put("message", String.format("Field images[%d] can't be blank.", i));
                return response;
            }
            images.add(params.get(key));
            i++;
            key = String.format("images[%d]", i);
        }
        if (images.size() == 0) {
            images = null;
        }
        // optional validate categories
        Set<String> categories = new HashSet<>();
        i = 0;
        key = String.format("categories[%d]", i);
        while (params.containsKey(key)) {
            if (params.get(key).isBlank()) {
                response.put("message", String.format("Field categories[%d] can't be blank.", i));
                return response;
            }
            categories.add(params.get(key).toLowerCase());
            i++;
            key = String.format("categories[%d]", i);
        }
        if (categories.size() == 0) {
            categories = null;
        }
        // optional enabled
        Boolean enabled = null;
        if (params.containsKey("enabled")) {
            enabled = Boolean.parseBoolean(params.get("enabled"));
        }

        // all validations test passed

        // read or create the product brand
        ProductBrand productBrand = null;
        if (brand != null) {
            productBrand = productService.readProductBrandByBrand(brand);
            if (productBrand == null) {
                productBrand = new ProductBrand();
                productBrand.setBrand(brand);
                if (!productService.createProductBrand(productBrand)) {
                    response.put("message", "Database couldn't register the product brand.");
                    return response;
                }
            }
        }

        // read or create the product categories
        Set<ProductCategory> productCategories = null;
        if (categories != null) {
            productCategories = new HashSet<>();
            for (String c : categories) {
                ProductCategory productCategory = productService.readProductCategoryByCategory(c);
                if (productCategory == null) {
                    productCategory = new ProductCategory();
                    productCategory.setCategory(c);
                    if (!productService.createProductCategory(productCategory)) {
                        response.put("message", "Database couldn't register the product category.");
                        return response;
                    }
                }
            }
        }

        // update the product
        if (productBrand != null) product.setProductBrand(productBrand);
        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (discount != null) product.setDiscount(discount);
        if (stock != null) product.setStock(stock);
        if (thumbnail != null) product.setThumbnailURL(thumbnail);
        if (images != null) product.setImagesURL(images);
        for (ProductCategory pc : productCategories) {
            product.addProductCategory(pc);
            pc.addProduct(product);
        }
        if (enabled != null) product.setEnabled(enabled);

        if (!productService.updateProduct(product)) {
            response.put("message", "Database couldn't update the product.");
            return response;
        }

        // product got updated
        response.put("error", false);
        return response;
    }

    @DeleteMapping("/admin/product")
    public HashMap<String, Object> deleteProduct(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate code
        if (!params.containsKey("code")) {
            response.put("message", "Field code is required.");
            return response;
        }
        String code = params.get("code");
        if (code.isBlank()) {
            response.put("message", "Field code can't be blank.");
            return response;
        }
        Product product = productService.readProductByCode(code);
        if (product == null) {
            response.put("message", "Field code must contain a valid product code.");
            return response;
        }

        // all validations test passed

        // delete the product
        if (!productService.deleteProduct(product)) {
            response.put("message", "Database couldn't delete the product.");
            return response;
        }

        // product got deleted
        response.put("error", false);
        return response;
    }

}
