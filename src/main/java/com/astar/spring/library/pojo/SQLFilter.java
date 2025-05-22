package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.LogicalOperator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public sealed abstract class SQLFilter permits Filter, MultiFilter {
    //    TODO IMPLEMENT
    private LogicalOperator combineWithPrevious;
}
