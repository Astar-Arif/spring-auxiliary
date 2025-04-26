package com.astar.spring.library.repository;

import com.astar.spring.library.pojo.Filter;
import com.astar.spring.library.utils.DatabaseUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigInteger;
import java.util.List;
//TODO IMPROVE
public class BaseRepositoryExtensionImpl<T> implements BaseRepositoryExtension<T> {

    protected final EntityManager entityManager;
    private final Class<T> clazz;

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
        Predicate predicate = DatabaseUtility.createPredicates(criteriaBuilder,root, filters);
        if (predicate != null){
            criteriaQuery.where(predicate);
        }
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    @Override
    public T fetchEntity(List<Filter> filter) {
        return null;
    }

    @Override
    public List<T> fetchEntities(List<Filter> filters) {
        return List.of();
    }

    @Override
    public BigInteger deleteWithConditions(List<Filter> filters) {
        return null;
    }

    @Override
    public BigInteger deleteWithPredicate(List<Filter> filters) {
        return null;
    }

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
