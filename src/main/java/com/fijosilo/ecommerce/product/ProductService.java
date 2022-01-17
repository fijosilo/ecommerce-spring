package com.fijosilo.ecommerce.product;

import com.fijosilo.ecommerce.category.Category;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(@Qualifier("JPAProductRepository") ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public boolean createProduct(Product product) {
        return productDAO.createProduct(product);
    }

    public Product readProductByCode(String code) {
        return productDAO.readProductByCode(code);
    }

    public List<Product> readProductsByFilters(String name, Double minPrice, Double maxPrice, String brand,
                                               List<String> categories, Integer maxProductsPerPage, Integer pageNumber) {
        return productDAO.readProductsByFilters(name, minPrice, maxPrice, brand, categories, maxProductsPerPage, pageNumber);
    }

    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(Product product) {
        return productDAO.deleteProduct(product);
    }

    public boolean createProductBrand(ProductBrand productBrand) {
        return productDAO.createProductBrand(productBrand);
    }

    public ProductBrand readProductBrandByBrand(String brand) {
        return productDAO.readProductBrandByBrand(brand);
    }

    public boolean createProductCategory(Category productCategory) {
        return productDAO.createProductCategory(productCategory);
    }

    public Category readProductCategoryByCategory(String category) {
        return productDAO.readProductCategoryByCategory(category);
    }

    public List<Product> readProductsByDescendingDate(Integer maxProductsPerPage, Integer pageNumber) {
        return productDAO.readProductsByDescendingDate(maxProductsPerPage, pageNumber);
    }

    public List<Product> readProductsByDescendingDiscount(Integer maxProductsPerPage, Integer pageNumber) {
        return productDAO.readProductsByDescendingDiscount(maxProductsPerPage, pageNumber);
    }

}
