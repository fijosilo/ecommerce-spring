package com.fijosilo.ecommerce.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("JPACategoryRepository")
@Transactional
public class CategoryRepository implements CategoryDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(CategoryRepository.class);

    @Override
    public boolean createCategory(Category category) {
        // if the category is already in the database don't do anything
        Category dbCategory = this.readCategoryByName(category.getName());
        if (dbCategory != null) {
            return true;
        }
        // else save the category to the database
        try {
            entityManager.persist(category);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Category readCategoryByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> builderQuery = criteriaBuilder.createQuery(Category.class);
        Root<Category> categoryRoot = builderQuery.from(Category.class);
        builderQuery.where(criteriaBuilder.equal(categoryRoot.get("name"), name));
        CriteriaQuery<Category> select = builderQuery.select(categoryRoot);
        TypedQuery<Category> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Category> categories = typedQuery.getResultList();
        return categories.isEmpty() ? null : categories.get(0);
    }

    @Override
    public Set<Category> readCategories() {
        // get a list of all categories
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> builderQuery = criteriaBuilder.createQuery(Category.class);
        Root<Category> categoryRoot = builderQuery.from(Category.class);
        TypedQuery<Category> typedQuery = entityManager.createQuery(builderQuery.select(categoryRoot));
        Set<Category> categories = new HashSet<>(typedQuery.getResultList());

        return categories.isEmpty() ? null : categories;
    }

    @Override
    public boolean updateCategory(Category category) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createCategory(category);
    }

    @Override
    public boolean deleteCategory(Category category) {
        // we are not going to delete the actual product records, just set their account as disabled
        category.setEnabled(false);
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createCategory(category);
    }

}
