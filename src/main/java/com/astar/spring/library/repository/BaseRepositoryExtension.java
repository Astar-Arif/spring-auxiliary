package com.astar.spring.library.repository;

import com.astar.spring.library.pojo.Filter;

import java.util.List;

public interface BaseRepositoryExtension<T> {
    T exists(List<Filter> filters);
    T fetchByPrinciple(Filter filter);
    List<T> fetchEntities(List<Filter> filters);
    T fetchEntity(Filter filter);
    void test();

}
