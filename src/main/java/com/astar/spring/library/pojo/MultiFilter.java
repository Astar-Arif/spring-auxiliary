package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.LogicalOperator;

import java.util.ArrayList;
import java.util.List;


public final class MultiFilter extends SQLFilter {
    private List<Filter> filters = new ArrayList<>();
    private LogicalOperator operator;
    private boolean isNegated;

    public MultiFilter(List<Filter> filters, LogicalOperator operator, boolean isNegated) {
        this.filters = filters;
        this.operator = operator;
        this.isNegated = isNegated;
    }

    public MultiFilter() {
    }

    public void addFilter(Filter filter) {
        filters.add(filter);

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

    public boolean isNegated() {
        return isNegated;
    }

    public void setNegated(boolean negated) {
        isNegated = negated;
    }
}
