package com.astar.spring.library.utils;

import com.astar.spring.library.pojo.Filter;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
//TODO TEST ALL THIS
/**
 * The type Database utility.
 */
public abstract class DatabaseUtility {

    /**
     * Get table name string.
     *
     * @param clazz the clazz
     * @return the string
     */
    public static String getTableName(EntityManager entityManager, Class<?> clazz) {
//        TODO CREATE EXCEPTION ENUMS;
        if (!isEntity(clazz)) {
            throw new IllegalArgumentException("Not An Entity");
        }
        SessionFactoryImplementor sessionFactoryImplementor = entityManager.unwrap(
                SessionFactoryImplementor.class);
        MappingMetamodel mappingMetamodel = sessionFactoryImplementor.getMappingMetamodel();
        EntityPersister entityPersister = mappingMetamodel.findEntityDescriptor(clazz);
        if (entityPersister == null){
            throw new IllegalArgumentException("Not An Entity");
        }
        if (entityPersister instanceof SingleTableEntityPersister) {
            return ((SingleTableEntityPersister) entityPersister).getTableName();
        }
        throw new IllegalArgumentException("Unable to find entity name");
    }

    /**
     * Is entity boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
//    TODO TEST THIS
    public static boolean isEntity(Class<?> clazz){
        return clazz.isAnnotationPresent(Entity.class);
    }

    /**
     * Get columns set.
     *
     * @param clazz the clazz
     * @return the set
     */

    public static Set<String> getColumns(EntityManager entityManager, Class<?> clazz){
        if (!isEntity(clazz)) throw new IllegalArgumentException("Not An Entity");

        EntityType<?> entityType = entityManager.getMetamodel().entity(clazz);
        return entityType
                .getAttributes()
                .stream()
                .map(Attribute::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Has column boolean.
     *
     * @param clazz  the clazz
     * @param column the column
     * @return the boolean
     */
    public static boolean hasColumn(EntityManager entityManager, Class<?> clazz, String column){
        Set<String> entityColumns = getColumns(entityManager, clazz);
        return entityColumns.contains(column);
    }

    /**
     * Get entity metadata map.
     *
     * @param clazz the clazz
     * @return the map
     */
    public static Map<String, Object> getEntityMetadata(Class<?> clazz){
        return null;
    }

    /**
     * Create predicate predicate.
     *
     * @param filter the filter
     * @return the predicate
     */
    public static Predicate createPredicate(Filter filter){
        return null;
    }

    /**
     * Create predicates predicate.
     *
     * @param filters the filters
     * @return the predicate
     */
    public static Predicate createPredicates(List<Filter> filters){
        return null;
    }

    /**
     * Predicate to string string.
     *
     * @param predicate the predicate
     * @return the string
     */
    public static String predicateToString(Predicate predicate){
        return null;
    }

    /**
     * Filter to predicate string.
     *
     * @param filters the filters
     * @return the string
     */
    public static String filterToPredicate(List<Filter> filters){
        return null;
    }

    /**
     *
     * @param filter
     * @return
     */
    private static Predicate helperCreateInPredicate(Filter filter){
        return null;
    }

    /**
     *
     * @param filter
     * @return
     */
    private static Predicate helperCreateLikePredicate(Filter filter){
        return null;
    }



}