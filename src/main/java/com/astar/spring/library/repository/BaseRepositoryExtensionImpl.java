package com.astar.spring.library.repository;

import com.astar.spring.library.pojo.Filter;
import com.astar.spring.library.utils.DatabaseUtility;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

//TODO IMPROVE


public class BaseRepositoryExtensionImpl<T> implements BaseRepositoryExtension<T> {


    protected final EntityManager entityManager;
    private final Class<T> clazz;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public BaseRepositoryExtensionImpl(EntityManager entityManager, Class<T> clazz) {
        this.entityManager = entityManager;
        this.clazz = clazz;
    }

    @Override
    public T exists(List<Filter> filters) {
        return null;
    }

    @Override
    public BigInteger countEntity(List<Filter> filters) {
        return null;
    }

    @Override
    public T fetchByPrinciple(List<Filter> filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        Predicate predicate = DatabaseUtility.createPredicates(criteriaBuilder, root, filters);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public T fetchEntity(List<Filter> filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        Predicate predicate = DatabaseUtility.createPredicates(criteriaBuilder, root, filters);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList.getFirst();
        }
    }

    @Override
    public List<T> fetchEntities(List<Filter> filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        Predicate predicate = DatabaseUtility.createPredicates(criteriaBuilder, root, filters);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    @Transactional
    public byte deleteEntity(List<Filter> filters) {
        T entity = fetchEntity(filters);
        if (entity != null) {
            entityManager.remove(entity);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    @Transactional
    public BigInteger deleteEntities(List<Filter> filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<T> criteriaDelete = criteriaBuilder.createCriteriaDelete(clazz);
        Root<T> root = criteriaDelete.from(clazz);
        Predicate predicate = DatabaseUtility.createPredicates(criteriaBuilder, root, filters);
        if (predicate != null){
            criteriaDelete.where(predicate);
        } else {
            LOGGER.warn("No predicate created in a delete statement");
        }
        Query query = entityManager.createQuery(criteriaDelete);
        entityManager.clear();
        return BigInteger.valueOf(query.executeUpdate());
    }
    // todo : dangerous, think it over
//    @Override
//    public byte updateEntities(List<Filter> filters, Map<String, Object> changes) {
//        return 0;
//    }


    @Override
    public void test() {
        try {
            String databaseName = (String) entityManager.createNativeQuery(
                    "SELECT current_database()").getSingleResult();
            System.out.println("Connected to database: " + databaseName);
        } catch (Exception e) {
            System.err.println("Error fetching database name: " + e.getMessage());
        }
    }
}
