package com.fijosilo.ecommerce.category;

import java.util.Set;

public interface CategoryDAO {
    boolean createCategory(Category category);
    Category readCategoryByName(String name);
    Set<Category> readCategories();
    boolean updateCategory(Category category);
    boolean deleteCategory(Category category);
}
