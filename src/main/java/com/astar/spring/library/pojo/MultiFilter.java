package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.LogicalOperator;

import java.util.ArrayList;
import java.util.List;


public final class MultiFilter extends SQLFilter {
    private List<Filter> filters = new ArrayList<>();
    private boolean isNegated;

    public MultiFilter(List<Filter> filters, LogicalOperator combineWithPrevious, boolean isNegated) {
        this.filters = filters;
        this.isNegated = isNegated;
        super.setCombineWithPrevious(combineWithPrevious);
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

    public boolean isNegated() {
        return isNegated;
    }

    public void setNegated(boolean negated) {
        isNegated = negated;
    }
}
