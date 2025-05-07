package com.astar.spring.library.repository;

import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.pojo.SQLFilter;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BaseRepositoryInterface<T, ID> {


    <S extends SQLFilter> List<T> findAll(S filter);

    <S extends SQLFilter> Page<T> findAll(
            List<S> filters, Pageable pageable,
            LogicalOperator logicalOperator
    );

    <S extends SQLFilter> Optional<T> findOne(S filter);

    <S extends SQLFilter> Optional<T> findOne(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> T findRequiredOne(S filter) throws Exception;

    <S extends SQLFilter> T findRequiredOne(
            List<S> filters, LogicalOperator logicalOperator) throws Exception;

    <S extends SQLFilter> T findNullableOne(S filter);

    <S extends SQLFilter> T findNullableOne(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> long count(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> boolean exists(List<S> filters, LogicalOperator logicalOperator);

    @Transactional
    <S extends SQLFilter> long delete(S filter);

    <S extends SQLFilter> long delete(List<S> filters, LogicalOperator logicalOperator);

    <S extends SQLFilter> long update(
            List<S> filters, Map<String, Object> changes,
            LogicalOperator logicalOperator
    );

    void test();
}
