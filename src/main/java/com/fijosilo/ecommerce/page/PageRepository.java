package com.fijosilo.ecommerce.page;

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
public class PageRepository implements PageDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(PageRepository.class);

    @Override
    public boolean createPage(Page page) {
        // if the page is already in the database don't do anything
        Page dbPage = this.readPageByTitle(page.getTitle());
        if (dbPage != null) {
            return true;
        }
        // else save the page to the database
        try {
            entityManager.persist(page);
            return true;
        } catch (IllegalArgumentException | PersistenceException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Page readPageByTitle(String title) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Page> builderQuery = criteriaBuilder.createQuery(Page.class);
        Root<Page> pageRoot = builderQuery.from(Page.class);
        builderQuery.where(criteriaBuilder.equal(pageRoot.get("title"), title));
        CriteriaQuery<Page> select = builderQuery.select(pageRoot);
        TypedQuery<Page> typedQuery = entityManager.createQuery(select).setMaxResults(1);
        List<Page> pages = typedQuery.getResultList();
        return pages.isEmpty() ? null : pages.get(0);
    }

    @Override
    public boolean updatePage(Page page) {
        // with JPA if the entity was loaded from the database
        // modifying it modifies it in the database, so we don't need to do anything
        // all we can do is make sure the entity is saved to the database
        return this.createPage(page);
    }

    @Override
    public boolean deletePage(Page page) {
        try {
            entityManager.remove(page);
            return true;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

}
