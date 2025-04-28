package com.astar.spring.library.repository;

import com.astar.spring.library.pojo.Filter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface BaseRepositoryExtension<T> {
    /**
     * @param filters
     * @return
     */
    T exists(List<Filter> filters);

    /**
     * @param filters
     * @return
     */
    BigInteger countEntity(List<Filter> filters);

    /**
     * Fetches a single entity based on filters.
     *
     * Returns the entity if found, null if no result, throws Exception if multiple results or other error.
     *
     * @param filters List of filters to apply.
     * @return The found entity or null.
     * @throws Exception if more than one result is found or another error occurs.
     */
    T fetchByPrinciple(List<Filter> filters);

    /**
     * Fetches an entity based on a list of filters.
     *
     * Returns the first entity found if multiple match the criteria,
     * or the single entity if only one matches,
     * or null if no entity matches the criteria.
     *
     * @param filters A list of filters to apply to the query.
     * @return The first matching entity, or null if no entity is found.
     */
    T fetchEntity(List<Filter> filters);

    /**
     * @param filters
     * @return
     */
    List<T> fetchEntities(List<Filter> filters);


    /**
     * @param filters
     * @return
     */
    byte deleteEntity(List<Filter> filters);

    /**
     * @param filters
     * @return
     */
    BigInteger deleteEntities(List<Filter> filters);

//    /**
//     *
//     * @param filters
//     * @param changes
//     * @return
//     */
//    byte updateEntities(List<Filter> filters, Map<String, Object> changes);


    /**
     *
     */
    void test();
}
