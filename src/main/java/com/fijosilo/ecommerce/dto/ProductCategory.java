package com.fijosilo.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_category", uniqueConstraints = @UniqueConstraint(columnNames = {"category"}))
public class ProductCategory {
    @Id
    @GenericGenerator(name="increment", strategy="increment")
    @GeneratedValue(generator = "increment")
    private int id;
    private String category;

    @JsonIgnore
    @ManyToMany(mappedBy = "productCategories")
    private Set<Product> products = new HashSet<>();

    public ProductCategory() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Product> getProducts() {
        return products;
    }

}
