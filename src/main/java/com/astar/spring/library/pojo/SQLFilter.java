package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.LogicalOperator;


public sealed abstract class SQLFilter permits Filter, MultiFilter {
    //    TODO IMPLEMENT
    private LogicalOperator combineWithPrevious;

    public LogicalOperator getCombineWithPrevious() {
        return combineWithPrevious;
    }

    public void setCombineWithPrevious(LogicalOperator combineWithPrevious) {
        this.combineWithPrevious = combineWithPrevious;
    }
}
