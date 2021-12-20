package com.fijosilo.ecommerce.repository;

import com.fijosilo.ecommerce.dao.UserDAO;
import com.fijosilo.ecommerce.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository("JPAUserRepository")
@Transactional
public class JPAUserRepository implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(JPAUserRepository.class);

    @Override
    public boolean createUser(User user) {
        // if the user is already in the database don't do anything
        User dbUser = this.readUserByEmail(user.getEmail());
        if (dbUser != null) {
            return false;
        }
        // else save the user to the database
        try {
            entityManager.persist(user);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public User readUserByEmail(String email) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cQuery = cBuilder.createQuery(User.class);
        Root<User> root = cQuery.from(User.class);
        cQuery.where(cBuilder.equal(root.get("email"), email));
        CriteriaQuery<User> select = cQuery.select(root);
        TypedQuery<User> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<User> userList = typedQuery.getResultList();
        return userList.isEmpty() ? null : userList.get(0);
    }

    @Override
    public boolean updateUser(User user) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createUser(user);
    }

    @Override
    public boolean deleteUser(User user) {
        // we are not going to delete the actual client records, just set their account as disabled
        user.setEnabled(false);
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createUser(user);
    }

}
