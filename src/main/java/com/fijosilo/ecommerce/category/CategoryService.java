package com.fijosilo.ecommerce.category;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService(@Qualifier("JPACategoryRepository") CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public boolean createCategory(Category category) {
        return categoryDAO.createCategory(category);
    }

    public Category readCategoryByName(String name) {
        return categoryDAO.readCategoryByName(name);
    }

    public Set<Category> readCategories() {
        return categoryDAO.readCategories();
    }

    public boolean updateCategory(Category category) {
        return categoryDAO.updateCategory(category);
    }

    public boolean deleteCategory(Category category) {
        return categoryDAO.deleteCategory(category);
    }

}
