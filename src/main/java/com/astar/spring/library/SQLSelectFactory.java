package com.astar.spring.library;

import jakarta.persistence.criteria.JoinType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class SQLSelectFactory {

    private List<String> columns = new ArrayList<>();
    private String from;
    private List<JOIN> joins = new ArrayList<>();
    private List<CONDITION> conditions = new ArrayList<>();


    private void addColumn(String col) {
        this.columns.add(col);
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<JOIN> getJoins() {
        return joins;
    }

    public void setJoins(List<JOIN> joins) {
        this.joins = joins;
    }

    public List<CONDITION> getConditions() {
        return conditions;
    }

    public void setConditions(List<CONDITION> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "SELECT ";
    }

    public static class Builder {
        SQLSelectFactory factory = new SQLSelectFactory();

        public Builder column(String col) {
            this.factory.addColumn(col);
            return this;
        }

        public Builder from(String fr) {
            this.factory.setFrom(fr);
            return this;
        }

        public SQLSelectFactory build() {
            return factory;
        }

    }

    private static class JOIN {
        private JoinType type;
        private String joinTable;
        private Pair<String, String> joinOn;
        public JOIN(JoinType type, String joinTable, Pair<String, String> joinOn) {
            this.type = type;
            this.joinTable = joinTable;
            this.joinOn = joinOn;
        }
    }

    private static class CONDITION {
        String column;
        String parameter;
        Object value;
        public CONDITION(String column, String parameter, Object value) {
            this.column = column;
            this.parameter = parameter;
            this.value = value;
        }
    }
}
