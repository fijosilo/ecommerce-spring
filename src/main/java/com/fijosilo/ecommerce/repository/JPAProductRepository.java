package com.fijosilo.ecommerce.repository;

import com.fijosilo.ecommerce.dao.ProductDAO;
import com.fijosilo.ecommerce.dto.Product;
import com.fijosilo.ecommerce.dto.ProductBrand;
import com.fijosilo.ecommerce.dto.ProductCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository("JPAProductRepository")
@Transactional
public class JPAProductRepository implements ProductDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(JPAProductRepository.class);

    @Override
    public boolean createProduct(Product product) {
        // if the product is already in the database don't do anything
        Product dbProduct = this.readProductByCode(product.getCode());
        if (dbProduct != null) {
            return true;
        }
        // else save the product to the database
        try {
            entityManager.persist(product);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Product readProductByCode(String code) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cQuery = cBuilder.createQuery(Product.class);
        Root<Product> root = cQuery.from(Product.class);
        cQuery.where(cBuilder.equal(root.get("code"), code));
        CriteriaQuery<Product> select = cQuery.select(root);
        TypedQuery<Product> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Product> productList = typedQuery.getResultList();
        return productList.isEmpty() ? null : productList.get(0);
    }

    @Override
    public boolean updateProduct(Product product) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createProduct(product);
    }

    @Override
    public boolean deleteProduct(Product product) {
        // we are not going to delete the actual product records, just set their account as disabled
        product.setEnabled(false);
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createProduct(product);
    }

    @Override
    public boolean createProductBrand(ProductBrand productBrand) {
        // if the product brand is already in the database don't do anything
        ProductBrand dbProductBrand = this.readProductBrandByBrand(productBrand.getBrand());
        if (dbProductBrand != null) {
            return true;
        }
        // else save the product brand to the database
        try {
            entityManager.persist(productBrand);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public ProductBrand readProductBrandByBrand(String brand) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductBrand> cQuery = cBuilder.createQuery(ProductBrand.class);
        Root<ProductBrand> root = cQuery.from(ProductBrand.class);
        cQuery.where(cBuilder.equal(root.get("brand"), brand));
        CriteriaQuery<ProductBrand> select = cQuery.select(root);
        TypedQuery<ProductBrand> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<ProductBrand> productBrandList = typedQuery.getResultList();
        return productBrandList.isEmpty() ? null : productBrandList.get(0);
    }

    @Override
    public boolean createProductCategory(ProductCategory productCategory) {
        // if the product category is already in the database don't do anything
        ProductCategory dbProductCategory = this.readProductCategoryByCategory(productCategory.getCategory());
        if (dbProductCategory != null) {
            return true;
        }
        // else save the product category to the database
        try {
            entityManager.persist(productCategory);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public ProductCategory readProductCategoryByCategory(String category) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductCategory> cQuery = cBuilder.createQuery(ProductCategory.class);
        Root<ProductCategory> root = cQuery.from(ProductCategory.class);
        cQuery.where(cBuilder.equal(root.get("category"), category));
        CriteriaQuery<ProductCategory> select = cQuery.select(root);
        TypedQuery<ProductCategory> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<ProductCategory> productCategoryList = typedQuery.getResultList();
        return productCategoryList.isEmpty() ? null : productCategoryList.get(0);
    }

}
