package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.product.Product;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class OrderProduct {
    @Id
    @GenericGenerator(name = "increment", strategy = "increment")
    @GeneratedValue(generator = "increment")
    private Long id;
    @ManyToOne
    private Product product;
    private Double price;
    private Double discount;

    public OrderProduct() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

}
