package com.fijosilo.ecommerce.order;

import com.fijosilo.ecommerce.authentication.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Transactional;
import java.util.List;

@Repository("JPAOrderRepository")
@Transactional
public class JPAOrderRepository implements OrderDAO{
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(JPAOrderRepository.class);

    @Override
    public boolean createOrder(Order order) {
        // if the product is already in the database don't do anything
        Order dbOrder = this.readOrderByCode(order.getCode());
        if (dbOrder != null) {
            return true;
        }
        // else save the product to the database
        try {
            entityManager.persist(order);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Order readOrderByCode(String code) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> builderQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> orderRoot = builderQuery.from(Order.class);
        builderQuery.where(criteriaBuilder.equal(orderRoot.get("code"), code));
        CriteriaQuery<Order> select = builderQuery.select(orderRoot);
        TypedQuery<Order> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Order> orderList = typedQuery.getResultList();
        return orderList.isEmpty() ? null : orderList.get(0);
    }

    @Override
    public List<Order> readOrdersByClientId(Long clientId, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);

        Metamodel metamodel = entityManager.getMetamodel();
        EntityType Order_ = metamodel.entity(Order.class);
        Root<Order> order = criteriaQuery.from(Order_);
        EntityType Client_ = metamodel.entity(Client.class);
        Join<Order, Client> client = order.join(Order_.getSingularAttribute("client"));

        TypedQuery<Order> typedQuery = entityManager.createQuery(
                criteriaQuery.select(order).where(criteriaBuilder.equal(client.get("id"), clientId)));
        if (offset != null) {
            typedQuery.setFirstResult(offset);
        }
        if (limit != null) {
            typedQuery.setMaxResults(limit);
        }
        List<Order> orderList = typedQuery.getResultList();
        return orderList;
    }

    @Override
    public boolean updateOrder(Order order) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createOrder(order);
    }

    @Override
    public boolean deleteOrder(Order order) {
        // we are not going to delete the actual order record, just set their fulfilled property to true
        order.setFulfilled(true);
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createOrder(order);
    }

}
