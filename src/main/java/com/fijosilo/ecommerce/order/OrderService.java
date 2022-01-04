package com.fijosilo.ecommerce.order;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderDAO orderDAO;

    public OrderService(@Qualifier("JPAOrderRepository") OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public boolean createOrder(Order order) {
        return orderDAO.createOrder(order);
    }

    public Order readOrderByCode(String code) {
        return orderDAO.readOrderByCode(code);
    }

    public List<Order> readOrdersByClientId(Long clientId, Integer offset, Integer limit) {
        return orderDAO.readOrdersByClientId(clientId, offset, limit);
    }

    public boolean updateOrder(Order order) {
        return orderDAO.updateOrder(order);
    }

    public boolean deleteOrder(Order order) {
        return orderDAO.deleteOrder(order);
    }

}
