package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;

import java.sql.Timestamp;
import java.util.List;

public interface OrderDAO {
    boolean createOrder(Order order);
    Order readOrderByCode(String code);
    List<Order> readOrdersByClient(Client client, Integer maxOrdersPerPage, Integer pageNumber);
    List<Order> readOrdersByFilters(Client client, Timestamp minDate, Timestamp maxDate, PaymentMethod paymentMethod,
                                    Boolean isPaid, Boolean isFulfilled, Integer maxOrdersPerPage, Integer pageNumber);
    boolean updateOrder(Order order);
    boolean deleteOrder(Order order);
}
