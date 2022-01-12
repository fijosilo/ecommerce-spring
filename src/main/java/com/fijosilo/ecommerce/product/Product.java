package com.fijosilo.ecommerce.product;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Product {
    @Id
    @GenericGenerator(name="increment", strategy="increment")
    @GeneratedValue(generator = "increment")
    private Long id;
    private String code;
    @ManyToOne
    private ProductBrand productBrand;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String thumbnailURL;

    private double discount;
    @ElementCollection
    private List<String> imagesURL;
    @ManyToMany
    private Set<ProductCategory> productCategories = new HashSet<>();
    private Long additionDate;
    private boolean isEnabled;

    public Product() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProductBrand getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(ProductBrand productBrand) {
        this.productBrand = productBrand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public List<String> getImagesURL() {
        return imagesURL;
    }

    public void setImagesURL(List<String> imagesURL) {
        this.imagesURL = imagesURL;
    }

    public Set<ProductCategory> getProductCategories() {
        return productCategories;
    }

    public void addProductCategory(ProductCategory productCategory) {
        productCategories.add(productCategory);
    }

    public Long getAdditionDate() {
        return additionDate;
    }

    public void setAdditionDate(Long additionDate) {
        this.additionDate = additionDate;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

}
