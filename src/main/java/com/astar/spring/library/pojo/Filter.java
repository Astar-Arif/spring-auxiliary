package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.SQLOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class Filter extends SQLFilter{
    private String field;
    private SQLOperator Operator;
    private Object value;
//    TODO IMPLEMENT THIS IN DATABASE UTILITY
    private boolean isNegated;

}

