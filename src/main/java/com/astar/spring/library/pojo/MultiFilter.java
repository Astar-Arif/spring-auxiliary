package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.LogicalOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public final class MultiFilter extends SQLFilter {
    private List<Filter> filters = new ArrayList<>();
    private LogicalOperator operator;
    private boolean isNegated;

    public void addFilter(Filter filter) {
        filters.add(filter);

    }
}
