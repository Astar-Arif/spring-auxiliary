package com.astar.spring.library.repository;

import com.astar.spring.library.pojo.Filter;

import java.math.BigInteger;
import java.util.List;

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
     * @param filter
     * @return
     */
    T fetchByPrinciple(List<Filter> filter);

    /**
     * @param filter
     * @return
     */
    T fetchEntity(List<Filter> filter);

    /**
     * @param filters
     * @return
     */
    List<T> fetchEntities(List<Filter> filters);

    /**
     * @param filters
     * @return
     */
    BigInteger deleteWithConditions(List<Filter> filters);

    /**
     * @param filters
     * @return
     */
    BigInteger deleteWithPredicate(List<Filter> filters);

    /**
     *
     */
    void test();
}
