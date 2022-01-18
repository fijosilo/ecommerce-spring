package com.fijosilo.ecommerce.category;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // get category
    @GetMapping("/category")
    public HashMap<String, Object> readCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate name
        if (!params.containsKey("category_name")) {
            response.put("message", "Field category_name is required.");
            return response;
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            response.put("message", "Field category_name can't be blank.");
            return response;
        }
        Category category = categoryService.readCategoryByName(categoryName);
        if (category == null) {
            response.put("message", "Field category_name must be a valid category name.");
            return response;
        }

        // all validations test passed

        response.put("error", false);
        response.put("category", category);
        return response;
    }

    // get categories
    @GetMapping("/categories")
    public HashMap<String, Object> readCategories() {
        HashMap<String, Object> response = new HashMap<>();

        // read categories
        Set<Category> categories = categoryService.readCategories();

        response.put("error", false);
        response.put("categories", categories);
        return response;
    }

    // post category
    @PostMapping("/category")
    public HashMap<String, Object> addCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate category name
        if (!params.containsKey("category_name")) {
            response.put("message", "Field category_name is required.");
            return response;
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            response.put("message", "Field category_name can't be blank.");
            return response;
        }
        Category category = categoryService.readCategoryByName(categoryName);
        if (category != null) {
            response.put("message", "A category with the provided category_name already exists in the database.");
            return response;
        }

        // optional validate parent category
        Category parent = null;
        if (params.containsKey("category_parent_name")) {
            String categoryParentName = params.get("category_parent_name");
            if (categoryParentName.isBlank()) {
                response.put("message", "Field category_parent_name can't be blank.");
                return response;
            }
            parent = categoryService.readCategoryByName(categoryParentName);
            if (parent == null) {
                response.put("message", "Field category_parent_name must be a valid category name.");
                return response;
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
            response.put("message", "Database couldn't register the category.");
            return response;
        }

        // category got registered
        response.put("error", false);
        return response;
    }

    // put category
    @PutMapping("/category")
    public HashMap<String, Object> updateCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate category
        if (!params.containsKey("category_name")) {
            response.put("message", "Field category_name is required.");
            return response;
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            response.put("message", "Field category_name can't be blank.");
            return response;
        }
        Category category = categoryService.readCategoryByName(categoryName);
        if (category == null) {
            response.put("message", "Field category_name must be a valid category name.");
            return response;
        }

        // optional validate name
        String newCategoryName = null;
        if (params.containsKey("category_new_name")) {
            newCategoryName = params.get("category_new_name");
            if (newCategoryName.isBlank()) {
                response.put("message", "Field category_new_name can't be blank.");
                return response;
            }
        }

        // optional validate parent
        Category parent = null;
        boolean isParentNull = false;
        if (params.containsKey("category_parent_name")) {
            String categoryParentName = params.get("category_parent_name");
            if (categoryParentName.isBlank()) {
                response.put("message", "Field category_parent_name can't be blank.");
                return response;
            }
            if (categoryParentName.equals("null")) {
                isParentNull = true;
            } else {
                parent = categoryService.readCategoryByName(categoryParentName);
                if (parent == null) {
                    response.put("message", "Field category_parent_name must be a valid category name.");
                    return response;
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
            response.put("message", "Database couldn't update the category.");
            return response;
        }

        // category got updated
        response.put("error", false);
        return response;
    }

    // delete category
    @DeleteMapping("/category")
    public HashMap<String, Object> deleteCategory(@RequestParam HashMap<String, String> params) {
        HashMap<String, Object> response = new HashMap<>();
        // if any validation fails response is going to have error = true
        response.put("error", true);

        // validate name
        if (!params.containsKey("category_name")) {
            response.put("message", "Field category_name is required.");
            return response;
        }
        String categoryName = params.get("category_name");
        if (categoryName.isBlank()) {
            response.put("message", "Field category_name can't be blank.");
            return response;
        }
        Category category = categoryService.readCategoryByName(categoryName);
        if (category == null) {
            response.put("message", "Field category_name must be a valid category name.");
            return response;
        }

        // all validations test passed

        // delete the category
        if (!categoryService.deleteCategory(category)) {
            response.put("message", "Database couldn't delete the category.");
            return response;
        }

        // category got deleted
        response.put("error", false);
        return response;
    }

}
