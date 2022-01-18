package com.fijosilo.ecommerce.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fijosilo.ecommerce.product.Product;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Category {
    @Id
    @GenericGenerator(name="increment", strategy="increment")
    @GeneratedValue(generator = "increment")
    private Long id;
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();
    @ManyToOne
    private Category parent;
    private Boolean isEnabled;

    public Category() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category category) {
        parent = category;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

}
