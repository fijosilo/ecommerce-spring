package com.fijosilo.ecommerce.dto;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="product", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Product {
    @Id
    @GenericGenerator(name="increment", strategy="increment")
    @GeneratedValue(generator = "increment")
    private int id;
    private String code;
    private String name;
    private String description;
    private double price;
    private double discount;
    private int stock;
    private String thumbnailURL;
    @ElementCollection
    private List<String> imagesURL;
    private boolean isEnabled;
}
