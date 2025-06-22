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

    public boolean getIsNegated() {
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

    public static Filter.Builder init(String field) {
        Filter.Builder builder = new Filter.Builder();
        builder.field(field);
        return builder;
    }

    public static Filter.Builder and(String field) {
        Filter.Builder builder = new Filter.Builder();
        builder.field(field);
        builder.and();
        return builder;
    }

    public static Filter.Builder or(String field) {
        Filter.Builder builder = new Filter.Builder();
        builder.field(field);
        builder.or();
        return builder;
    }


    public static Filter not(Filter filter) {
        filter.setNegated(true);
        return filter;
    }

    public static void main(String[] args) {
        Filter fil = Filter.and("Hhahaha").op(SQLOperator.EQUALS, "KKAKKAKKA").build();
        Filter fil1 = new Filter.Builder()
                .and()
                .field("haha")
                .op(SQLOperator.EQUALS, "KAKA")
                .build();
        System.out.print(fil.toString());
        System.out.print(fil1.toString());
    }

    public static class Builder {
        Filter filter = new Filter();

        public Builder field(String field) {
            this.filter.setField(field);
            return this;
        }

        public Builder not() {
            this.filter.setNegated(!this.filter.isNegated);
            return this;
        }

        public Builder joinType(JoinType joinType) {
            this.filter.setJoinType(joinType);
            return this;
        }

        public Builder and() {
            this.filter.setCombineWithPrevious(LogicalOperator.AND);
            return this;
        }
        public Builder or() {
            this.filter.setCombineWithPrevious(LogicalOperator.OR);
            return this;
        }

        public Filter build() {
            return this.filter;
        }

        public Builder op(SQLOperator operator, Object value) {
            this.filter.setOperator(operator);
            this.filter.setValue(value);
            return this;
        }
    }
}

