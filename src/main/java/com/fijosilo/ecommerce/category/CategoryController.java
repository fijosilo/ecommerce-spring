package com.fijosilo.ecommerce.category;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;

@Controller
@RequestMapping(value = "/admin")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate name
        if (!params.containsKey("category_name")) {
            payload.put("error", "Field category_name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            payload.put("error", "Field category_name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate category
        Category category = categoryService.readCategoryByName(categoryName);
        if (category == null) {
            payload.put("error", "Field category_name must be a valid category name.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        payload.put("category", category);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> readCategories() {
        HashMap<String, Object> payload = new HashMap<>();

        // read categories
        Set<Category> categories = categoryService.readCategories();

        payload.put("categories", categories);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> createCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate name
        if (!params.containsKey("category_name")) {
            payload.put("error", "Field category_name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            payload.put("error", "Field category_name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate that the category doesn't exist
        Category category = categoryService.readCategoryByName(categoryName);
        if (category != null) {
            payload.put("error", "A category with the provided category_name already exists in the database.");
            return new ResponseEntity<>(payload, HttpStatus.CONFLICT);
        }

        // optional validate parent category
        Category parent = null;
        if (params.containsKey("category_parent_name")) {
            String categoryParentName = params.get("category_parent_name");
            if (categoryParentName.isBlank()) {
                payload.put("error", "Field category_parent_name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            parent = categoryService.readCategoryByName(categoryParentName);
            if (parent == null) {
                payload.put("error", "Field category_parent_name must be a valid category name.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate enabled
        boolean enabled = true;
        if (params.containsKey("enabled")) {
            enabled = Boolean.parseBoolean(params.get("enabled"));
        }

        // all validations test passed

        // create and save the category
        category = new Category();
        category.setName(categoryName);
        category.setParent(parent);
        category.setEnabled(enabled);
        if (!categoryService.createCategory(category)) {
            payload.put("error", "Database couldn't register the category.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // category got registered
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
    }

    @PutMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> updateCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate name
        if (!params.containsKey("category_name")) {
            payload.put("error", "Field category_name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            payload.put("error", "Field category_name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        // validate category
        Category category = categoryService.readCategoryByName(categoryName);
        if (category == null) {
            payload.put("error", "Field category_name must be a valid category name.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // optional validate new name
        String newCategoryName = null;
        if (params.containsKey("category_new_name")) {
            newCategoryName = params.get("category_new_name");
            if (newCategoryName.isBlank()) {
                payload.put("error", "Field category_new_name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        // optional validate new parent
        Category parent = null;
        boolean isParentNull = false;
        if (params.containsKey("category_parent_name")) {
            String categoryParentName = params.get("category_parent_name");
            if (categoryParentName.isBlank()) {
                payload.put("error", "Field category_parent_name can't be blank.");
                return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            categoryParentName = categoryParentName.toUpperCase();
            if (categoryParentName.equals("NULL")) {
                isParentNull = true;
            } else {
                parent = categoryService.readCategoryByName(categoryParentName);
                if (parent == null) {
                    payload.put("error", "Field category_parent_name must be a valid category name.");
                    return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
        }

        // optional validate enabled
        Boolean enabled = null;
        if (params.containsKey("enabled")) {
            enabled = Boolean.parseBoolean(params.get("enabled"));
        }

        // all validations test passed

        // update the category
        if (newCategoryName != null) category.setName(newCategoryName);
        if (isParentNull || parent != null) category.setParent(parent);
        if (enabled != null) category.setEnabled(enabled);
        if (!categoryService.updateCategory(category)) {
            payload.put("error", "Database couldn't update the category.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // category got updated
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @DeleteMapping(value = "/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> deleteCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> payload = new HashMap<>();

        // validate name
        if (!params.containsKey("category_name")) {
            payload.put("error", "Field category_name is required.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            payload.put("error", "Field category_name can't be blank.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Category category = categoryService.readCategoryByName(categoryName);
        if (category == null) {
            payload.put("error", "Field category_name must be a valid category name.");
            return new ResponseEntity<>(payload, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // all validations test passed

        // delete the category
        if (!categoryService.deleteCategory(category)) {
            payload.put("error", "Database couldn't delete the category.");
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // category got deleted
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

}
