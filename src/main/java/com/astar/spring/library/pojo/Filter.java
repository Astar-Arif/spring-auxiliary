package com.astar.spring.library.pojo;

import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.enums.SQLOperator;
import jakarta.persistence.criteria.JoinType;


public final class Filter extends SQLFilter {

    private String field;
    private SQLOperator operator;
    private Object value;
    //    TODO IMPLEMENT THIS IN DATABASE UTILITY
    private boolean isNegated;
    private JoinType joinType;

    public Filter() {
    }

    public Filter(
            String field, SQLOperator operator, Object value, boolean isNegated,
            JoinType joinType, LogicalOperator combineWithPrevious
    ) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.isNegated = isNegated;
        this.joinType = joinType;
        super.setCombineWithPrevious(combineWithPrevious);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SQLOperator getOperator() {
        return operator;
    }

    public void setOperator(SQLOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isNegated() {
        return isNegated;
    }

    public void setNegated(boolean negated) {
        isNegated = negated;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public static class Builder {
        Filter filter = new Filter();

        public Builder field(String field) {
            this.filter.setField(field);
            return this;
        }

        public Builder SQLOperator(SQLOperator sqlOperator) {
            this.filter.setOperator(sqlOperator);
            return this;
        }

        public Builder value(Object value) {
            this.filter.setValue(value);
            return this;
        }

        public Builder isNegated(boolean isNegated) {
            this.filter.setNegated(isNegated);
            return this;
        }

        public Builder joinType(JoinType joinType) {
            this.filter.setJoinType(joinType);
            return this;
        }

        public Builder combineWithPrevious(LogicalOperator combine) {
            this.filter.setCombineWithPrevious(combine);
            return this;
        }

        public Filter build() {
            return this.filter;
        }
    }
}

