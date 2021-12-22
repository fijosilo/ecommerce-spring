package com.fijosilo.ecommerce.dao;

import com.fijosilo.ecommerce.dto.Product;
import com.fijosilo.ecommerce.dto.ProductBrand;
import com.fijosilo.ecommerce.dto.ProductCategory;

public interface ProductDAO {
    boolean createProduct(Product product);
    Product readProductByCode(String code);
    boolean updateProduct(Product product);
    boolean deleteProduct(Product product);

    boolean createProductBrand(ProductBrand productBrand);
    ProductBrand readProductBrandByBrand(String brand);

    boolean createProductCategory(ProductCategory productCategory);
    ProductCategory readProductCategoryByCategory(String category);
}
