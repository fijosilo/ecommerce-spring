package com.fijosilo.ecommerce.category;

import java.util.List;

public interface CategoryDAO {
    boolean createCategory(Category category);
    Category readCategoryByName(String name);
    List<Category> readCategories();
    boolean updateCategory(Category category);
    boolean deleteCategory(Category category);
}
