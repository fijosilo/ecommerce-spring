package com.fijosilo.ecommerce.address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.List;

@Repository("JPAAddressRepository")
@Transactional
public class JPAAddressRepository implements AddressDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(JPAAddressRepository.class);

    @Override
    public Address readAddressById(Long id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Address> builderQuery = criteriaBuilder.createQuery(Address.class);
        Root<Address> addressRoot = builderQuery.from(Address.class);
        builderQuery.where(criteriaBuilder.equal(addressRoot.get("id"), id));
        CriteriaQuery<Address> select = builderQuery.select(addressRoot);
        TypedQuery<Address> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Address> addresses = typedQuery.getResultList();
        return addresses.isEmpty() ? null : addresses.get(0);
    }

    @Override
    public boolean createAddress(Address address) {
        // if the product is already in the database don't do anything
        Address dbAddress = this.readAddressById(address.getId());
        if (dbAddress != null) {
            return true;
        }
        // else save the product to the database
        try {
            entityManager.persist(address);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateAddress(Address address) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createAddress(address);
    }

    @Override
    public boolean deleteAddress(Address address) {
        if (address == null) return false;
        try {
            entityManager.remove(address);
            return true;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

}
