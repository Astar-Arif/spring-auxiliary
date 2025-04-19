package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.SQLOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Filter {
    @Getter
    private String field;
    @Getter
    private SQLOperator Operator;
    @Getter
    private Object value;

}

