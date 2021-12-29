package com.fijosilo.ecommerce.service;

import com.fijosilo.ecommerce.dao.ProductDAO;
import com.fijosilo.ecommerce.dto.Product;
import com.fijosilo.ecommerce.dto.ProductBrand;
import com.fijosilo.ecommerce.dto.ProductCategory;
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

    public boolean createProductCategory(ProductCategory productCategory) {
        return productDAO.createProductCategory(productCategory);
    }

    public ProductCategory readProductCategoryByCategory(String category) {
        return productDAO.readProductCategoryByCategory(category);
    }

}
