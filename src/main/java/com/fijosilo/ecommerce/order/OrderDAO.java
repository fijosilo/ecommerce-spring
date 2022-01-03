package com.fijosilo.ecommerce.order;

import java.util.List;

public interface OrderDAO {
    boolean createOrder(Order order);
    Order readOrderById(Long id);
    List<Order> readOrdersByClientId(Long clientId, Integer offset, Integer limit);
    boolean updateOrder(Order order);
    boolean deleteOrder(Order order);
}
