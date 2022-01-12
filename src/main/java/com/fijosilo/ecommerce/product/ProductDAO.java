package com.fijosilo.ecommerce.product;

import java.util.List;

public interface ProductDAO {
    boolean createProduct(Product product);
    Product readProductByCode(String code);
    List<Product> readProductsByFilters(String name, Double minPrice, Double maxPrice, String brand,
                                        List<String> categories, Integer maxProductsPerPage, Integer pageNumber);
    boolean updateProduct(Product product);
    boolean deleteProduct(Product product);

    boolean createProductBrand(ProductBrand productBrand);
    ProductBrand readProductBrandByBrand(String brand);

    boolean createProductCategory(ProductCategory productCategory);
    ProductCategory readProductCategoryByCategory(String category);

    List<Product> readProductsByDescendingDate(Integer maxProductsPerPage, Integer pageNumber);
    List<Product> readProductsByDescendingDiscount(Integer maxProductsPerPage, Integer pageNumber);
}
