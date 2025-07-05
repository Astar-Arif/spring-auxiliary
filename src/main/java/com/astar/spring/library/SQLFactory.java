package com.astar.spring.library;

import com.astar.java.library.utils.StringUtility;
import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.enums.SQLOperator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// IDEA : GENERATE STRING QUERY ON THE FLY WHEN CALLING ANY BUILDER FUNCTIONS
public class SQLFactory {
    //TODO IMPLEMENT MULIT COLUMN AGGREGATE FUNCTION

    //    C:INSERT, R:SELECT, U:UPDATE, D:DELETE
    private static final SQLFactory.DATABASE_PRODUCT DATABASE = null;
    private static final String INSERT_OP = "INSERT";
    private static final String SELECT_OP = "SELECT";
    private static final String UPDATE_OP = "UPDATE";
    private static final String DELETE_OP = "DELETE";

    public static class INSERT {
        private List<Pair<SQLFactory.COLUMN, Pair<String, Object>>> columnParameterValueList = new ArrayList<>();
        private SQLFactory.TABLE table;
        private boolean isAll = true;

        private void setTable(TABLE table) {
            this.table = table;
        }

        public String toStringQuery() {
            StringBuilder c = new StringBuilder();
            StringBuilder v = new StringBuilder();
            c.append(SQLFactory.INSERT_OP).append(" INTO ");
            c.append(this.table.toString());
            if (!this.isAll)
                c.append(" (");
            v.append("VALUES (");
            boolean isFirst = true;
            for (Pair<SQLFactory.COLUMN, Pair<String, Object>> ele : this.columnParameterValueList) {
                SQLFactory.COLUMN col = ele.getLeft();
                Pair<String, Object> parameterValue = ele.getRight();
                if (!isFirst) {
                    c.append(", ");
                    v.append(", ");
                }
                if (!this.isAll)
                    c.append(col.toString());
                v.append(parameterValue.getLeft());
                if (isFirst) isFirst = false;
            }
            if (!this.isAll)
                c.append(')');
            v.append(")");
            return c.append(' ').append(v).append(';').toString();
        }

        public static class Builder {
            private SQLFactory.INSERT insertPrototype = new SQLFactory.INSERT();

            public Builder table(String table) {
                this.insertPrototype.setTable(new SQLFactory.TABLE(table));
                return this;
            }

            public Builder value(String column, String parameter, Object value) {
                if (!StringUtility.isEmpty(column))
                    this.insertPrototype.isAll = false;
                this.insertPrototype.columnParameterValueList.add(
                        Pair.of(new SQLFactory.COLUMN(column), Pair.of(parameter, value)));
                return this;
            }

            public Builder value(String parameter, Object value) {
                this.insertPrototype.isAll = true;
                this.insertPrototype.columnParameterValueList.add(
                        Pair.of(null, Pair.of(parameter, value)));
                return this;
            }

            public SQLFactory.INSERT build() {
                return this.insertPrototype;
            }

            public SQLFactory.INSERT get() {
                return this.insertPrototype;
            }

        }
    }

    //TODO IMPLEMENT HAVING CONDITIONS
    public static class SELECT {
        private List<SQLFactory.COLUMN> columns = new ArrayList<>();
        private SQLFactory.TABLE table;
        private List<SQLFactory.CONDITIONS> conditionsList = new ArrayList<>();
        private List<SQLFactory.JOIN> joinList = new ArrayList<>();
        private List<SQLFactory.COLUMN> groupList = new ArrayList<>();
        private List<SQLFactory.ORDER> orderList = new ArrayList<>();

        private void addColumn(SQLFactory.COLUMN column) {
            this.columns.add(column);
        }

        private void addConditions(SQLFactory.CONDITIONS conditions) {
            this.conditionsList.add(conditions);
        }

        private void addJoin(SQLFactory.JOIN join) {
            this.joinList.add(join);
        }

        private void addGroup(SQLFactory.COLUMN column) {
            this.groupList.add(column);
        }

        private void setTable(SQLFactory.TABLE table) {
            this.table = table;
        }

        public static String union(SQLFactory.SELECT... selects) {
            StringBuilder b = new StringBuilder();
            b.append(Arrays.stream(selects).map(ele -> ele.toStringQuery(true)).collect(
                    Collectors.joining(" UNION ")));
            return b.append(';').toString();
        }

        public static class Builder {
            SQLFactory.SELECT selectPrototype = new SQLFactory.SELECT();

            public Builder column(String col) {
                this.selectPrototype.addColumn(new SQLFactory.COLUMN(col));
                return this;
            }

            public Builder columnAggr(
                    SQLFactory.AGGREGATE_FUNCTION aggregateFunction, String column) {
                this.selectPrototype.addColumn(
                        new SQLFactory.COLUMN(aggregateFunction, column));
                return this;
            }

            public Builder conditions(
                    LogicalOperator combineWithPrevious,
                    SQLFactory.CONDITIONS conditions_1
            ) {
                conditions_1.setCombineWithPrevious(combineWithPrevious);
                this.selectPrototype.addConditions(conditions_1);
                return this;
            }

            public Builder conditions(SQLFactory.CONDITIONS conditions_1) {
                this.selectPrototype.addConditions(conditions_1);
                return this;
            }

            public Builder table(String table) {
                this.selectPrototype.setTable(new SQLFactory.TABLE(table));
                return this;
            }

            public Builder table(SQLFactory.TABLE table) {
                this.selectPrototype.setTable(table);
                return this;
            }

            public Builder join(
                    SQLFactory.JOIN_TYPE joinType, String table,
                    String foreignColumn, String primaryColumn
            ) {
                this.selectPrototype.addJoin(
                        new SQLFactory.JOIN(joinType, table, foreignColumn, primaryColumn));
                return this;
            }

            public Builder innerJoin(String table, String foreignColumn, String targetColumn) {
                this.selectPrototype.addJoin(new SQLFactory.JOIN(
                        SQLFactory.JOIN_TYPE.INNER, table, foreignColumn, targetColumn
                ));
                return this;
            }

            public Builder leftJoin(String table, String foreignColumn, String targetColumn) {
                this.selectPrototype.addJoin(new SQLFactory.JOIN(
                        SQLFactory.JOIN_TYPE.LEFT, table, foreignColumn, targetColumn
                ));
                return this;
            }

            public Builder rightJoin(String table, String foreignColumn, String targetColumn) {
                this.selectPrototype.addJoin(new SQLFactory.JOIN(
                        SQLFactory.JOIN_TYPE.RIGHT, table, foreignColumn, targetColumn
                ));
                return this;
            }

            public Builder fullJoin(String table, String foreignColumn, String targetColumn) {
                this.selectPrototype.addJoin(new SQLFactory.JOIN(
                        SQLFactory.JOIN_TYPE.FULL, table, foreignColumn, targetColumn
                ));
                return this;
            }

            public Builder crossJoin(String table, String foreignColumn, String targetColumn) {
                this.selectPrototype.addJoin(new SQLFactory.JOIN(
                        SQLFactory.JOIN_TYPE.CROSS, table, foreignColumn, targetColumn
                ));
                return this;
            }

            public Builder naturalJoin(String table, String foreignColumn, String targetColumn) {
                this.selectPrototype.addJoin(new SQLFactory.JOIN(
                        SQLFactory.JOIN_TYPE.NATURAL, table, foreignColumn, targetColumn
                ));
                return this;
            }

            public Builder selfJoin(String table, String foreignColumn, String targetColumn) {
                this.selectPrototype.addJoin(new SQLFactory.JOIN(
                        SQLFactory.JOIN_TYPE.SELF, table, foreignColumn, targetColumn
                ));
                return this;
            }

            public Builder groupBy(String column) {
                this.selectPrototype.addGroup(new SQLFactory.COLUMN(column));
                return this;
            }

            public Builder order(String column, SORT_DIRECTION direction) {
                this.selectPrototype.addOrder(new SQLFactory.COLUMN(column), direction);
                return this;
            }

            public SQLFactory.SELECT build() {
                return this.selectPrototype;
            }

            public SQLFactory.SELECT get() {
                return this.selectPrototype;
            }
        }

        private void addOrder(COLUMN column, SORT_DIRECTION direction) {
            this.orderList.add(new SQLFactory.ORDER(column, direction));
        }

        public String toStringQuery(boolean isUnion) {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.SELECT_OP).append(' ');
            b.append(this.columns.stream().map(SQLFactory.COLUMN::toString).collect(
                    Collectors.joining(", "))).append(' ');
            b.append("FROM").append(' ');
            b.append(this.table.toString());
            if (!this.conditionsList.isEmpty()) {
                b.append(" WHERE");
                for (SQLFactory.CONDITIONS conditions_1 : this.conditionsList) {
                    b.append(' ').append(conditions_1.toString());
                }
            }
            if (!this.joinList.isEmpty()) {
                for (SQLFactory.JOIN join : this.joinList) {
                    b.append(' ').append(join.toString());
                }
            }

            if (!this.groupList.isEmpty()) {
                b.append(" GROUP BY").append(' ');
                b.append(this.groupList.stream().map(SQLFactory.COLUMN::toString).collect(
                        Collectors.joining(", ")));
            }

            if (!this.orderList.isEmpty()) {
                b.append(" ORDER BY").append(' ');
                b.append(this.orderList.stream().map(SQLFactory.ORDER::toString).collect(
                        Collectors.joining(", ")));

            }
            if (isUnion)
                return b.toString();
            return b.append(';').toString();
        }

        public String toPrettyStringQuery() {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.SELECT_OP).append('\n');
            b.append(this.columns.stream().map(col -> '\t' + col.toString()).collect(
                    Collectors.joining(",\n"))).append('\n');
            b.append("FROM").append('\n');
            b.append('\t').append(this.table.toString());
            if (!this.conditionsList.isEmpty()) {
                b.append("\nWHERE");
                for (SQLFactory.CONDITIONS conditions_1 : this.conditionsList) {
                    b.append(conditions_1.toPrettyString());
                }
                b.append('\n');
            }
            if (!this.joinList.isEmpty()) {
                for (SQLFactory.JOIN join : this.joinList) {
                    b.append("\n").append(join.toPrettyString());
                }
            }
            if (!this.groupList.isEmpty()) {
                b.append("\nGROUP BY").append('\n');
                b.append(this.groupList.stream().map(ele -> '\t' + ele.toString()).collect(
                        Collectors.joining(",\n")));
            }
            if (!this.orderList.isEmpty()) {
                b.append("\nORDER BY").append('\n');
                b.append(this.orderList.stream().map(ele -> '\t' + ele.toString()).collect(
                        Collectors.joining(",\n")));
            }
            return b.append(';').toString();
        }
    }

    public static class UPDATE {
        private List<Pair<SQLFactory.COLUMN, Pair<String, Object>>> columnParameterValueList = new ArrayList<>();
        private SQLFactory.TABLE table;
        private List<SQLFactory.CONDITIONS> conditionsList = new ArrayList<>();

        private void addConditions(CONDITIONS conditions1) {
            this.conditionsList.add(conditions1);
        }

        public static class Builder {
            private SQLFactory.UPDATE updatePrototype = new SQLFactory.UPDATE();


            public Builder set(String column, String parameter, Object value) {
                this.updatePrototype.columnParameterValueList.add(
                        Pair.of(new SQLFactory.COLUMN(column), Pair.of(parameter, value)));
                return this;
            }

            public Builder conditions(SQLFactory.CONDITIONS conditions_1) {
                this.updatePrototype.addConditions(conditions_1);
                return this;
            }

            public Builder table(String table) {
                this.updatePrototype.table = new SQLFactory.TABLE(table);
                return this;
            }

            public SQLFactory.UPDATE build() {
                return this.updatePrototype;
            }

            public SQLFactory.UPDATE get() {
                return this.updatePrototype;
            }
        }

        public String toStringQuery() {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.UPDATE_OP).append(' ');
            b.append(this.table.toString());
            b.append(" SET").append(' ');
            b.append(this.columnParameterValueList.stream().map(ele -> {
                SQLFactory.COLUMN cTemp = ele.getLeft();
                Pair<String, Object> parameterObject = ele.getRight();
                return cTemp.toString() + " = " + parameterObject.getLeft();
            }).collect(Collectors.joining(", ")));
            if (!this.conditionsList.isEmpty()) {
                b.append(" WHERE");
                for (SQLFactory.CONDITIONS conditions_1 : this.conditionsList) {
                    b.append(' ').append(conditions_1.toString());
                }
            }
            return b.append(';').toString();
        }
    }

    public static class DELETE {
        private SQLFactory.TABLE table;
        private List<SQLFactory.CONDITIONS> conditionsList = new ArrayList<>();

        private void setTable(TABLE table) {
            this.table = table;
        }

        private void addConditions(CONDITIONS conditions1) {
            this.conditionsList.add(conditions1);
        }

        public static class Builder {


            private SQLFactory.DELETE deletePrototype = new SQLFactory.DELETE();

            public Builder table(String table) {
                this.deletePrototype.setTable(new SQLFactory.TABLE(table));
                return this;
            }

            public Builder conditions(SQLFactory.CONDITIONS conditions_1) {
                this.deletePrototype.addConditions(conditions_1);
                return this;
            }

            public SQLFactory.DELETE build() {
                return this.deletePrototype;
            }

            public SQLFactory.DELETE get() {
                return this.deletePrototype;
            }

        }

        public String toQueryString() {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.DELETE_OP).append("FROM ");
            b.append(this.table.toString());
            if (!this.conditionsList.isEmpty()) {
                b.append(" WHERE");
                for (SQLFactory.CONDITIONS conditions_1 : this.conditionsList) {
                    b.append(' ').append(conditions_1.toString());
                }
            }
            return b.append(';').toString();
        }
    }

    private static class COLUMN {
        private SQLFactory.AGGREGATE_FUNCTION aggregateFunction;
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

        public COLUMN(SQLFactory.AGGREGATE_FUNCTION aggregateFunction, String column) {
            this.aggregateFunction = aggregateFunction;
            this.name = column;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.aggregateFunction != null) {
                StringBuilder c = new StringBuilder();
                if (!StringUtility.isEmpty(this.correlation))
                    c.append(this.correlation).append('.');
                c.append(this.name);
                String cRes = c.toString();
                b.append(
                        String.format(this.aggregateFunction.getParameterizedStringFormat(), cRes));
            } else {
                if (!StringUtility.isEmpty(this.correlation))
                    b.append(this.correlation).append('.');
                b.append(this.name);
            }
            if (!StringUtility.isEmpty(this.alias)) b.append(" AS ").append(this.alias);
            return b.toString();
        }

        public static SQLFactory.COLUMN generate(String name) {
            return new SQLFactory.COLUMN(name);
        }

        public static SQLFactory.COLUMN generate(String correlation, String name) {
            return new SQLFactory.COLUMN(correlation, name, null);
        }

        public static SQLFactory.COLUMN generate(
                String correlation, String name, String alias) {
            return new SQLFactory.COLUMN(correlation, name, alias);
        }
    }

    public static class TABLE {
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

        public static SQLFactory.TABLE generate(String name) {
            return new SQLFactory.TABLE(name);
        }

        public static SQLFactory.TABLE generate(String correlation, String name) {
            return new SQLFactory.TABLE(correlation, name, null);
        }

        public static SQLFactory.TABLE generate(
                String correlation, String name, String alias) {
            return new SQLFactory.TABLE(correlation, name, alias);
        }
    }

    public static class CONDITION {
        private String column;
        private SQLOperator op;
        private Map<String, Object> mapParameterValue;
        private LogicalOperator combineWithPrevious;


        public CONDITION(
                LogicalOperator combineWithPrevious, String column, SQLOperator op,
                Map<String, Object> mapParameterValue

        ) {
            this.combineWithPrevious = combineWithPrevious;
            this.column = column;
            this.op = op;
            this.mapParameterValue = mapParameterValue;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.combineWithPrevious != null) b.append(this.combineWithPrevious).append(' ');
            b.append(this.column).append(' ');
            b.append(String.format(this.op.getParameterizedStringFormat(),
                                   this.mapParameterValue.keySet().toArray()));
            return b.toString();
        }
    }

    public static class CONDITIONS {
        private List<CONDITION> conditionList = new ArrayList<>();
        private LogicalOperator combineWithPrevious;

        public LogicalOperator getCombineWithPrevious() {
            return this.combineWithPrevious;
        }

        public void setCombineWithPrevious(LogicalOperator combineWithPrevious) {
            this.combineWithPrevious = combineWithPrevious;
        }

        public void add(SQLFactory.CONDITION condition) {
            this.conditionList.add(condition);
        }

        public static SQLFactory.CONDITIONS init(
                String column, SQLOperator op, Map<String, Object> mapParameterValue) {
            SQLFactory.CONDITIONS conditions_1 = new SQLFactory.CONDITIONS();
            conditions_1.add(new SQLFactory.CONDITION(null, column, op, mapParameterValue));
            return conditions_1;
        }

        public SQLFactory.CONDITIONS and(
                String column, SQLOperator op, Map<String, Object> mapParameterValue) {
            this.add(new SQLFactory.CONDITION(LogicalOperator.AND, column, op,
                                              mapParameterValue));
            return this;
        }

        public SQLFactory.CONDITIONS or(
                String column, SQLOperator op, Map<String, Object> mapParameterValue) {
            this.add(new SQLFactory.CONDITION(LogicalOperator.OR, column, op,
                                              mapParameterValue));
            return this;

        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.combineWithPrevious != null) b.append(this.combineWithPrevious).append(' ');
            b.append('(').append(
                    this.conditionList.stream().map(SQLFactory.CONDITION::toString).collect(
                            Collectors.joining(" "))).append(')');
            return b.toString();
        }


        public String toPrettyString() {
            StringBuilder b = new StringBuilder();
            if (this.combineWithPrevious != null) b.append('\n').append(this.combineWithPrevious);
            b.append("\n\t").append('(').append(
                    this.conditionList.stream().map(SQLFactory.CONDITION::toString).collect(
                            Collectors.joining(" "))).append(')');
            return b.toString();
        }
    }

    public static class JOIN {
        private SQLFactory.JOIN_TYPE joinType;
        private SQLFactory.TABLE table;
        private SQLFactory.COLUMN foreignColumn;
        private SQLFactory.COLUMN primaryColumn;

        public JOIN(JOIN_TYPE joinType, TABLE table, COLUMN foreignColumn, COLUMN primaryColumn) {
            this.joinType = joinType;
            this.table = table;
            this.foreignColumn = foreignColumn;
            this.primaryColumn = primaryColumn;
        }

        public JOIN(JOIN_TYPE joinType, String table, String foreignColumn, String primaryColumn) {
            this.joinType = joinType;
            this.table = new SQLFactory.TABLE(table);
            this.foreignColumn = new SQLFactory.COLUMN(foreignColumn);
            this.primaryColumn = new SQLFactory.COLUMN(primaryColumn);
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(this.joinType.toString()).append(" JOIN ");
            b.append(this.table.toString()).append(' ');
            b.append(this.foreignColumn.toString()).append(" ON ").append(
                    this.primaryColumn.toString());
            return b.toString();
        }

        public String toPrettyString() {
            StringBuilder b = new StringBuilder();
            b.append(this.joinType.toString()).append(" JOIN ");
            b.append("\n\t").append(this.table.toString()).append(' ');
            b.append(this.foreignColumn.toString()).append(" ON ").append(
                    this.primaryColumn.toString());
            return b.toString();
        }
    }

    public static class ORDER {
        private SQLFactory.COLUMN column;
        private SORT_DIRECTION direction;

        public ORDER(COLUMN column, SORT_DIRECTION direction) {
            this.column = column;
            this.direction = direction;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(this.column.toString()).append(' ').append(this.direction.toString());
            return b.toString();
        }
    }

    public enum SORT_DIRECTION {
        ASC,
        DESC
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

    public enum AGGREGATE_FUNCTION {
        // Source : https://www.postgresql.org/docs/9.5/functions-aggregate.html
        // Prioritising PostGresQL since I use Postgres
        ARRAY_AGG("ARRAY_AGG(%s)"),
        AVG("AVG(%s)"),
        BIT_AND("BIT_AND(%s)"),
        BIT_OR("BIT_OR(%s)"),
        BOOL_AND("BOOL_AND(%s)"),
        BOOL_OR("BOOL_OR(%s)"),
        COUNT("COUNT(%s)"),
        EVERY("EVERY(%s)"),
        JSON_AGG("JSON_AGG(%s)"),
        JSONB_AGG("JSONB_AGG(%s)"),
        JSON_OBJECT_AGG("JSON_OBJECT_AGG(%s)"),
        JSONB_OBJECT_AGG("JSONB_OBJECT_AGG(%s)"),
        MAX("MAX(%s)"),
        MIN("MIN(%s)"),
        STRING_AGG("STRING_AGG(%s)"),
        SUM("SUM(%s)"),
        XMLAGG("XMLAGG(%s)"),
        COALESCE("COALESCE(%s)"),
        ANY("ANY(%s)"),
        SOME("SOME(%s)"),
        CORR("CORR(%s,%s)"),
        COVAR_POP("COVAR_POP(%s,%s)"),
        COVAR_SAMP("COVAR_SAMP(%s,%s)"),
        REGR_AVGX("REGR_AVGX(%s,%s)"),
        REGR_AVGY("REGR_AVGY(%s,%s)"),
        REGR_COUNT("REGR_COUNT(%s,%s)"),
        REGR_INTERCEPT("REGR_INTERCEPT(%s,%s)"),
        REGR_R2("REGR_R2(%s,%s)"),
        REGR_SLOPE("REGR_SLOPE(%s,%s)"),
        REGR_SXX("REGR_SXX(%s,%s)"),
        REGR_SXY("REGR_SXY(%s,%s)"),
        REGR_SYY("REGR_SYY(%s,%s)"),
        STDDEV("STDDEV(%s)"),
        STDDEV_SAMP("STDDEV_SAMP(%s)"),
        STDDEV_POP("STDDEV_POP(%s)"),
        VARIANCE("VARIANCE(%s)"),
        VAR_SAMP("VAR_SAMP(%s)"),
        VAR_POP("VAR_POP(%s)"),
        MODE("MODE(%s)"),
        PERCENTILE_CONT("PERCENTILE_CONT(%s)"),
        PERCENTILE_DISC("PERCENTILE_DISC(%s)"),
        RANK("RANK(%s)"),
        DENSE_RANK("DENSE_RANK(%s)"),
        PERCENT_RANK("PERCENT_RANK(%s)"),
        CUME_DIST("CUME_DIST(%s)"),
        GROUPING("GROUPING(%s)");
        private final String parameterizedStringFormat;

        AGGREGATE_FUNCTION(String parameterizedStringFormat) {
            this.parameterizedStringFormat = parameterizedStringFormat;
        }

        public String getParameterizedStringFormat() {
            return parameterizedStringFormat;
        }
    }


    public enum DATABASE_PRODUCT {
        POSTGRESQL, MYSQL, MARIADB
    }
}
