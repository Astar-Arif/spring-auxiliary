package com.astar.spring.library.repository;

import com.astar.spring.library.pojo.Filter;
import jakarta.persistence.EntityManager;

import java.util.List;

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
    public T fetchByPrinciple(Filter filter) {
        return null;
    }

    @Override
    public List<T> fetchEntities(List<Filter> filters) {
        return List.of();
    }

    @Override
    public T fetchEntity(Filter filter) {
        return null;
    }

    @Override
    public void test() {
        try {
            String databaseName = (String) entityManager.createNativeQuery("SELECT current_database()").getSingleResult();
            System.out.println("Connected to database: " + databaseName);
        } catch (Exception e) {
            System.err.println("Error fetching database name: " + e.getMessage());
        }
    }
}
