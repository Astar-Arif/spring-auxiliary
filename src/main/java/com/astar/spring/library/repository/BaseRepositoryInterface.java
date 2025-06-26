package com.astar.spring.library.repository;

import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.pojo.SQLFilter;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BaseRepositoryInterface<T, ID> extends JpaRepositoryImplementation<T, ID> {

    <S extends SQLFilter> Object query(S filter, String... columns);

    <S extends SQLFilter> List<Tuple> query(String... columns);

    <S extends SQLFilter> List<Tuple> query(List<S> filter, String... columns);

    <D> List<D> nativeQueryList(String query, Class<D> clazz);

    <D> D nativeQueryObject(String query, Class<D> clazz);

    <D> List<D> HQLList(String query, Class<D> clazz);

    <S extends SQLFilter> List<T> findAll(S filter);

    <S extends SQLFilter> List<T> findAll(List<S> filters);

    <S extends SQLFilter> Page<T> findAll(List<S> filters, Pageable pageable);

    <S extends SQLFilter> List<Tuple> findAll(S filter, Map<String, String> projections);

    <S extends SQLFilter> Optional<T> findOne(S filter);

    <S extends SQLFilter> Optional<T> findOne(List<S> filters);

    <S extends SQLFilter> List<Tuple> findOne(S filter, Map<String, String> projections);

    <S extends SQLFilter> BigInteger sum(String column, S filter);

    <S extends SQLFilter> BigInteger sum(String column, List<S> filters);

    <S extends SQLFilter> Double avg(String column, S filter);

    <S extends SQLFilter> Double avg(String column, List<S> filters);

    <S extends SQLFilter> BigInteger min(String column, S filter);

    <S extends SQLFilter> BigInteger min(String column, List<S> filters);

    <S extends SQLFilter> BigInteger max(String column, S filter);

    <S extends SQLFilter> BigInteger max(String column, List<S> filters);

    <S extends SQLFilter> long count(List<S> filters);

    <S extends SQLFilter> boolean exists(List<S> filters);

    @Transactional
    <S extends SQLFilter> long delete(S filter);

    <S extends SQLFilter> long delete(List<S> filters);

    <S extends SQLFilter> long update(
            List<S> filters, Map<String, Object> changes,
            LogicalOperator logicalOperator
    );

    void getDatabaseInfo();
}
