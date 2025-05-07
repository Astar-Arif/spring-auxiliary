package com.astar.spring.library.repository;

import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.pojo.SQLFilter;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BaseRepositoryInterface<T, ID>  {



    <S extends SQLFilter> List<T> findAll(S filter);

    <S extends SQLFilter> Page<T> findAll(List<S> filters, Pageable pageable,
                                          LogicalOperator logicalOperator
    );

    <S extends SQLFilter> Optional<T> findOne(S filter);

    <S extends SQLFilter> Optional<T> findOne(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> T findRequiredOne(S filter) throws Exception;

    <S extends SQLFilter> T findRequiredOne(List<S> filters, LogicalOperator logicalOperator) throws Exception;

    <S extends SQLFilter> T findNullableOne(S filter);

    <S extends SQLFilter> T findNullableOne(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> long count(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> boolean exists(List<S> filters, LogicalOperator logicalOperator);

    @Transactional
    <S extends SQLFilter> long delete(S filter);

    <S extends SQLFilter> long delete(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> long update(List<S> filters, Map<String, Object> changes,
                                      LogicalOperator logicalOperator
    );




//
//    /**
//     * Fetches a single entity based on filters.
//     *
//     * Returns the entity if found, null if no result, throws Exception if multiple results or other error.
//     *
//     * @param filters List of filters to apply.
//     * @return The found entity or null.
//     * @throws Exception if more than one result is found or another error occurs.
//     */
//    T fetchByPrinciple(List<Filter> filters);
//
//
//    /**
//     * Fetches an entity based on a list of filters.
//     *
//     * Returns the first entity found if multiple match the criteria,
//     * or the single entity if only one matches,
//     * or null if no entity matches the criteria.
//     *
//     * @param filters A list of filters to apply to the query.
//     * @return The first matching entity, or null if no entity is found.
//     */
//    T fetchEntity(List<Filter> filters);
//
//    /**
//     * @param filters
//     * @return
//     */
//    List<T> fetchEntities(List<Filter> filters);
//
//
//    /**
//     * @param filters
//     * @return
//     */
//    byte deleteEntity(List<Filter> filters);
//
//    /**
//     * @param filters
//     * @return
//     */
//    BigInteger deleteEntities(List<Filter> filters);
//
    ////    /**
    ////     *
    ////     * @param filters
    ////     * @param changes
    ////     * @return
    ////     */
    ////    byte updateEntities(List<Filter> filters, Map<String, Object> changes);
//


    /**
     *
     */
    void test();
}
