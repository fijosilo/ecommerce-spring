package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;
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

    public List<Order> readOrdersByClient(Client client, Integer maxOrdersPerPage, Integer pageNumber) {
        return orderDAO.readOrdersByClient(client, maxOrdersPerPage, pageNumber);
    }

    public List<Order> readOrdersByFilters(Client client, Long minDate, Long maxDate, PaymentMethod paymentMethod,
                                           Boolean isPaid, Boolean isFulfilled, Integer maxOrdersPerPage, Integer pageNumber) {
        return orderDAO.readOrdersByFilters(client, minDate, maxDate, paymentMethod,
                isPaid, isFulfilled, maxOrdersPerPage, pageNumber);
    }

    public boolean updateOrder(Order order) {
        return orderDAO.updateOrder(order);
    }

    public boolean deleteOrder(Order order) {
        return orderDAO.deleteOrder(order);
    }

}
