package com.astar.spring.library;

import com.astar.common.library.utils.StringUtility;
import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.enums.SQLOperator;
import org.hibernate.annotations.processing.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLFactoryEnhanced {

    //    C:INSERT, R:SELECT, U:UPDATE, D:DELETE
    private static final String INSERT_OP = "INSERT";
    private static final String SELECT_OP = "SELECT";
    private static final String UPDATE_OP = "UPDATE";
    private static final String DELETE_OP = "DELETE";


    public static class INSERT {
    }

    public static class SELECT {
        private List<SQLFactoryEnhanced.COLUMN> columns = new ArrayList<>();
        private SQLFactoryEnhanced.TABLE table;
        private List<SQLFactoryEnhanced.CONDITIONS> conditionsList = new ArrayList<>();


        private void addColumn(String columnStr) {
            this.columns.add(new SQLFactoryEnhanced.COLUMN(columnStr));
        }
        public static class Builder {
            SQLFactoryEnhanced.SELECT selectPrototype = new SQLFactoryEnhanced.SELECT();





            public Builder column (String col) {
                this.selectPrototype.addColumn(col);
                return this;
            }

            public Builder condition () {
                return this;
            }

            public Builder table() {
                return this;
            }




            public SQLFactoryEnhanced.SELECT build() {
                return this.selectPrototype;
            }

            public SQLFactoryEnhanced.SELECT get() {
                return this.selectPrototype;
            }
        }




    }

    public static class UPDATE {

    }

    public static class DELETE {

    }


    private static class COLUMN {
        private String correlation;
        private String name;
        private String alias;

        private COLUMN() {
        }

        private COLUMN(String name) {
            this.name = name;
        }

        private COLUMN(String correlation, String name, String alias) {
            this.correlation = correlation;
            this.name = name;
            this.alias = alias;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (!StringUtility.isEmpty(this.correlation)) b.append(this.correlation).append('.');
            b.append(this.name);
            if (!StringUtility.isEmpty(this.alias)) b.append(" AS ").append(this.alias);
            return b.toString();
        }

        public static SQLFactoryEnhanced.COLUMN generate(String name) {
            return new SQLFactoryEnhanced.COLUMN(name);
        }

        public static SQLFactoryEnhanced.COLUMN generate(String correlation, String name) {
            return new SQLFactoryEnhanced.COLUMN(correlation, name, null);
        }

        public static SQLFactoryEnhanced.COLUMN generate(
                String correlation, String name, String alias) {
            return new SQLFactoryEnhanced.COLUMN(correlation, name, alias);
        }
    }

    public static class CONDITION {
        private String column;
        private SQLOperator op;
        private String parameter;
        private Object value;

        private LogicalOperator combineWithPrevious;


        public CONDITION(
                String column, SQLOperator op, String parameter, Object value,
                LogicalOperator combineWithPrevious
        ) {
            this.column = column;
            this.op = op;
            this.parameter = parameter;
            this.value = value;
            this.combineWithPrevious = combineWithPrevious;
        }


        public static SQLFactoryEnhanced.CONDITION init(
                String column, SQLOperator op,
                String parameter, Object value
        ) {
            return new SQLFactoryEnhanced.CONDITION(column, op, parameter, value, null);
        }

        public static SQLFactoryEnhanced.CONDITION and(
                String column, SQLOperator op,
                String parameter, Object value
        ) {
            return new SQLFactoryEnhanced.CONDITION(column, op, parameter, value,
                                                    LogicalOperator.AND);
        }

        public static SQLFactoryEnhanced.CONDITION or(
                String column, SQLOperator op,
                String parameter, Object value
        ) {
            return new SQLFactoryEnhanced.CONDITION(column, op, parameter, value,
                                                    LogicalOperator.OR);
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.combineWithPrevious != null) b.append(this.combineWithPrevious).append(' ');
            b.append(this.column).append(' ');
            b.append(this.op.getSymbol()).append(' ');
            b.append(this.parameter);
            return b.toString();
        }
    }

    public static class CONDITIONS {
        private List<CONDITION> conditionList = new ArrayList<>();
        private LogicalOperator combineWithPrevious;

        public void setCombineWithPrevious(LogicalOperator combineWithPrevious) {
            this.combineWithPrevious = combineWithPrevious;
        }

        public void add(SQLFactoryEnhanced.CONDITION condition){
            this.conditionList.add(condition);
        }

        public static SQLFactoryEnhanced.CONDITIONS init(SQLFactoryEnhanced.CONDITION condition){
            SQLFactoryEnhanced.CONDITIONS conditions = new SQLFactoryEnhanced.CONDITIONS();
            conditions.add(condition);
            return conditions;
        }
        public static SQLFactoryEnhanced.CONDITIONS and(SQLFactoryEnhanced.CONDITION condition){
            SQLFactoryEnhanced.CONDITIONS conditions = new SQLFactoryEnhanced.CONDITIONS();
            conditions.add(condition);
            conditions.setCombineWithPrevious(LogicalOperator.AND);
            return conditions;
        }
        public static SQLFactoryEnhanced.CONDITIONS or(SQLFactoryEnhanced.CONDITION condition){
            SQLFactoryEnhanced.CONDITIONS conditions = new SQLFactoryEnhanced.CONDITIONS();
            conditions.add(condition);
            conditions.setCombineWithPrevious(LogicalOperator.OR);
            return conditions;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.combineWithPrevious != null) b.append(this.combineWithPrevious).append(' ');
            b.append(this.conditionList.stream().map(SQLFactoryEnhanced.CONDITION::toString).collect(
                    Collectors.joining(" ")));
            return b.toString();
        }
    }

    private static class TABLE {
        private String correlation;
        private String name;
        private String alias;

        public TABLE() {
        }

        public TABLE(String name) {
            this.correlation = correlation;
            this.name = name;
        }

        public TABLE(String correlation, String name, String alias) {
            this.correlation = correlation;
            this.name = name;
            this.alias = alias;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (!StringUtility.isEmpty(this.correlation)) b.append(this.correlation).append('.');
            b.append(this.name);
            if (!StringUtility.isEmpty(this.alias)) b.append(' ').append(this.alias);
            return b.toString();
        }

        public static SQLFactoryEnhanced.TABLE generate(String name) {
            return new SQLFactoryEnhanced.TABLE(name);
        }

        public static SQLFactoryEnhanced.TABLE generate(String correlation, String name) {
            return new SQLFactoryEnhanced.TABLE(correlation, name, null);
        }

        public static SQLFactoryEnhanced.TABLE generate(
                String correlation, String name, String alias) {
            return new SQLFactoryEnhanced.TABLE(correlation, name, alias);
        }
    }


    public enum JOIN_TYPE {
        INNER,
        LEFT,
        RIGHT,
        FULL,
        CROSS,
        NATURAL,
        SELF
    }


}
