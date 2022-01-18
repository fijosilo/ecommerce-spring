package com.fijosilo.ecommerce.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class InfoRepository implements InfoDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(InfoRepository.class);

    @Override
    public boolean createInfo(Info info) {
        // if the info is already in the database don't do anything
        Info dbInfo = this.readInfoByTitle(info.getTitle());
        if (dbInfo != null) {
            return true;
        }
        // else save the info to the database
        try {
            entityManager.persist(info);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Info readInfoByTitle(String title) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Info> builderQuery = criteriaBuilder.createQuery(Info.class);
        Root<Info> infoRoot = builderQuery.from(Info.class);
        builderQuery.where(criteriaBuilder.equal(infoRoot.get("title"), title));
        CriteriaQuery<Info> select = builderQuery.select(infoRoot);
        TypedQuery<Info> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Info> infoList = typedQuery.getResultList();
        return infoList.isEmpty() ? null : infoList.get(0);
    }

    @Override
    public boolean updateInfo(Info info) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createInfo(info);
    }

    @Override
    public boolean deleteInfo(Info info) {
        try {
            entityManager.remove(info);
            return true;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

}
