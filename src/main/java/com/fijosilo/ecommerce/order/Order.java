package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;
import com.fijosilo.ecommerce.product.Product;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "client_order")
public class Order {
    @Id
    @GenericGenerator(name="increment", strategy="increment")
    @GeneratedValue(generator = "increment")
    private Long id;
    private String code;
    @ManyToOne
    private Client client;
    @ElementCollection
    private List<OrderProduct> products = new LinkedList<>();
    private Timestamp date;
    private String chargeAddress;
    private String paymentMethod;
    private boolean isPaid;
    private String deliverAddress;
    private String status;
    private boolean fulfilled;

    public Order() {}

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<OrderProduct> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setPrice(product.getPrice());
        orderProduct.setDiscount(product.getDiscount());
        this.products.add(orderProduct);
    }

    public Timestamp getDate() {
        return date;
    }

    public Double getPrice() {
        Double price = 0.0;
        for (OrderProduct op : products) {
            price += op.getPrice() * (1.0 - op.getDiscount());
        }
        return price;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getChargeAddress() {
        return chargeAddress;
    }

    public void setChargeAddress(String chargeAddress) {
        this.chargeAddress = chargeAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

}
