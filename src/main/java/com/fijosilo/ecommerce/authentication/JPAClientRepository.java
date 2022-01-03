package com.fijosilo.ecommerce.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository("JPAClientRepository")
@Transactional
public class JPAClientRepository implements ClientDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(JPAClientRepository.class);

    @Override
    public boolean createClient(Client client) {
        // if the user is already in the database don't do anything
        Client dbClient = this.readClientByEmail(client.getEmail());
        if (dbClient != null) {
            return true;
        }
        // else save the user to the database
        try {
            entityManager.persist(client);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Client readClientByEmail(String email) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cQuery = cBuilder.createQuery(Client.class);
        Root<Client> root = cQuery.from(Client.class);
        cQuery.where(cBuilder.equal(root.get("email"), email));
        CriteriaQuery<Client> select = cQuery.select(root);
        TypedQuery<Client> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Client> clientList = typedQuery.getResultList();
        return clientList.isEmpty() ? null : clientList.get(0);
    }

    @Override
    public boolean updateClient(Client client) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createClient(client);
    }

    @Override
    public boolean deleteClient(Client client) {
        // we are not going to delete the actual client records, just set their account as disabled
        client.setEnabled(false);
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createClient(client);
    }

}
