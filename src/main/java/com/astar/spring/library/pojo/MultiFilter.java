package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.LogicalOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public final class MultiFilter extends SQLFilter{
    private List<Filter> filters;
    private LogicalOperator operator;
    private boolean isNegated;



    public MultiFilter() {}

    public MultiFilter(List<Filter> filters, LogicalOperator operator, boolean isNegated) {
        this.filters = filters;
        this.operator = operator;
        this.isNegated = isNegated;
    }

}
