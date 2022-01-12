package com.fijosilo.ecommerce.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Transactional;
import java.util.LinkedList;
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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> builderQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> productRoot = builderQuery.from(Product.class);
        builderQuery.where(criteriaBuilder.equal(productRoot.get("code"), code));
        CriteriaQuery<Product> select = builderQuery.select(productRoot);
        TypedQuery<Product> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Product> productList = typedQuery.getResultList();
        return productList.isEmpty() ? null : productList.get(0);
    }

    @Override
    public List<Product> readProductsByFilters(String name, Double minPrice, Double maxPrice, String brand,
                                               List<String> categories, Integer maxProductsPerPage, Integer pageNumber) {
        // initialize the query
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType Product_ = metamodel.entity(Product.class);
        Root<Product> product = criteriaQuery.from(Product_);

        // generate the query conditions
        List<Predicate> predicates = new LinkedList<>();
        if (name != null) {
            predicates.add(criteriaBuilder.like(product.get("name"), name));
        }
        if (minPrice != null && maxPrice != null) {
            predicates.add(criteriaBuilder.between(product.get("price"), minPrice, maxPrice));
        }
        if (brand != null) {
            Join<Product, ProductBrand> productBrand = product.join(Product_.getSingularAttribute("productBrand"));
            predicates.add(criteriaBuilder.equal(productBrand.get("brand"), brand));
        }
        if (categories != null) {
            EntityType ProductCategory_ = metamodel.entity(ProductCategory.class);
            SetJoin<Product, ProductCategory> productCategories = product.join(Product_.getSet("productCategories", ProductCategory.class));
            for (String category : categories) {
                productCategories.on(criteriaBuilder.equal(productCategories.get("category"), category));
            }
        }

        // execute query and get the result
        TypedQuery<Product> typedQuery = entityManager.createQuery(
                criteriaQuery.select(product).where(predicates.toArray(new Predicate[]{})));
        typedQuery.setFirstResult((pageNumber - 1) * maxProductsPerPage);
        typedQuery.setMaxResults(maxProductsPerPage);
        List<Product> productList = typedQuery.getResultList();
        return productList;
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

    @Override
    public List<Product> readProductsByDescendingDate(Integer maxProductsPerPage, Integer pageNumber) {
        // initialize the query
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType Product_ = metamodel.entity(Product.class);
        Root<Product> productRoot = criteriaQuery.from(Product_);

        // execute query and get the result
        TypedQuery<Product> typedQuery = entityManager.createQuery(
                criteriaQuery
                        .select(productRoot)
                        .where(criteriaBuilder.equal(productRoot.get("isEnabled"), true))
                        .orderBy(criteriaBuilder.desc(productRoot.get("additionDate")))
        );
        typedQuery.setFirstResult((pageNumber - 1) * maxProductsPerPage);
        typedQuery.setMaxResults(maxProductsPerPage);
        List<Product> productList = typedQuery.getResultList();
        return productList;
    }

}
