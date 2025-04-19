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
public class Filter {
    private String field;
    private SQLOperator Operator;
    private Object value;

}

