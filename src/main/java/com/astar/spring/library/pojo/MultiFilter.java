package com.astar.spring.library.pojo;

import java.util.List;

public class MultiFilter {
    private List<Filter> filters;
    private LogicalOperator operator;

    public enum LogicalOperator {
        AND,
        OR
    }

    public MultiFilter() {}

    public MultiFilter(List<Filter> filters, LogicalOperator operator) {
        this.filters = filters;
        this.operator = operator;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }
}
