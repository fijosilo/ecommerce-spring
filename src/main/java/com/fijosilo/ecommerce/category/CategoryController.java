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

        // optional validate parent category
        Category categoryParent = null;
        if (!params.containsKey("category_parent_name")) {
            String categoryParentName = params.get("category_parent_name");
            if (categoryParentName.isBlank()) {
                response.put("message", "Field category_parent_name can't be blank.");
                return response;
            }
            categoryParent = categoryService.readCategoryByName(categoryParentName);
            if (categoryParent == null) {
                response.put("message", "Field category_parent_name must be a valid category name.");
                return response;
            }
        }

        // all validations test passed

        // create and save the category
        Category category = categoryService.readCategoryByName(categoryName);
        if (category == null) {
            category = new Category();
            category.setName(categoryName);
            if (!categoryService.createCategory(category)) {
                response.put("message", "Database couldn't register the category.");
                return response;
            }
        }
        if (categoryParent != null) {
            if (!categoryParent.getSubCategories().contains(category)) {
                categoryParent.addSubCategory(category);
                if (!categoryService.updateCategory(categoryParent)) {
                    response.put("message", "Database couldn't update the parent category.");
                    return response;
                }
            }
        }

        // product got registered
        response.put("error", false);
        return response;
    }


    // put category
    @PutMapping("/category")
    public HashMap<String, Object> updateCategory(@RequestParam HashMap<String, String> params) {
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

        // optional validate new name
        String newCategoryName = null;
        if (!params.containsKey("category_new_name")) {
            newCategoryName = params.get("category_new_name");
            if (newCategoryName.isBlank()) {
                response.put("message", "Field category_new_name can't be blank.");
                return response;
            }
        }

        // optional validate parent category
        Category categoryParent = null;
        if (!params.containsKey("category_parent_name")) {
            String categoryParentName = params.get("category_parent_name");
            if (categoryParentName.isBlank()) {
                response.put("message", "Field category_parent_name can't be blank.");
                return response;
            }
            categoryParent = categoryService.readCategoryByName(categoryParentName);
            if (categoryParent == null) {
                response.put("message", "Field category_parent_name must be a valid category name.");
                return response;
            }
            if (!categoryParent.getSubCategories().contains(category)) {
                response.put("message", "Field category_parent_name must be a valid parent category name.");
                return response;
            }
        }

        // optional validate new parent category
        Category newCategoryParent = null;
        boolean isNewCategoryParentNull = false;
        if (!params.containsKey("category_new_parent_name")) {
            String newCategoryParentName = params.get("category_new_parent_name");
            if (newCategoryParentName.isBlank()) {
                response.put("message", "Field category_new_parent_name can't be blank.");
                return response;
            }
            if (newCategoryParentName.equals("null")) {
                isNewCategoryParentNull = true;
            } else {
                newCategoryParent = categoryService.readCategoryByName(newCategoryParentName);
                if (newCategoryParent == null) {
                    response.put("message", "Field category_new_parent_name must be a valid category name.");
                    return response;
                }
            }
        }

        // all validations test passed

        // update the category name
        if (newCategoryName != null) {
            category.setName(newCategoryName);
            response.put("message", "Database couldn't update the category.");
            return response;
        }
        // update the category parent
        if (categoryParent != null) {
            if (isNewCategoryParentNull) {
                categoryParent.remSubCategory(category);
                if (!categoryService.updateCategory(categoryParent)) {
                    response.put("message", "Database couldn't update the category.");
                    return response;
                }
            } else if (newCategoryParent != null) {
                categoryParent.remSubCategory(category);
                newCategoryParent.addSubCategory(category);
                if (!categoryService.updateCategory(categoryParent)) {
                    response.put("message", "Database couldn't update the current parent category.");
                    return response;
                }
                if (!categoryService.updateCategory(newCategoryParent)) {
                    response.put("message", "Database couldn't update the new parent category.");
                    return response;
                }
            }
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
