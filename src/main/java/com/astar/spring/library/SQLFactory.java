package com.astar.spring.library;

/*
 * TODO:
 * * 1. Implement UNION
 * * 2. Change using string concatenation [str + str]
 * *
 * *
 */

import com.astar.common.library.utils.StringUtility;
import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.enums.SQLOperator;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLFactory {


    private static final String OP_SELECT = "SELECT";
    private static final String OP_INSERT = "INSERT INTO";
    private static final String OP_UPDATE = "UPDATE";
    private static final String OP_DELETE = "DELETE";

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLFactory.class);

    public static SQLFactory.CONDITION createCondition(
            String column,
            SQLOperator op,
            String parameter,
            Object value,
            LogicalOperator combineWithPrevious
    ) {
        return new SQLFactory.CONDITION(
                column, op, parameter, value, combineWithPrevious
        );
    }

    public static SQLFactory.CONDITIONS createConditions(
            List<CONDITION> conditionList,
            LogicalOperator combineWithPrevious
    ) {
        return new SQLFactory.CONDITIONS(
                conditionList, combineWithPrevious
        );

    }

    public static SQLFactory.JOIN createJoin(
            TABLE joinTable,
            SQLFactory.JOIN_TYPE joinType,
            COLUMN joinColumn,
            SQLOperator joinOperator,
            COLUMN targetColumn
    ) {
        return new SQLFactory.JOIN(joinTable, joinType, joinColumn, joinOperator, targetColumn);
    }

    public static SQLFactory.JOIN createJoin(
            String joinTable,
            SQLFactory.JOIN_TYPE joinType,
            String joinColumn,
            SQLOperator joinOperator,
            String targetColumn
    ) {
        return new SQLFactory.JOIN(
                new TABLE(joinTable),
                joinType,
                new COLUMN(joinColumn),
                joinOperator,
                new COLUMN(targetColumn)
        );
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

    public static class INSERT {
        private List<SQLFactory.COLUMN> columns = new ArrayList<>();
        private SQLFactory.TABLE table;
        private List<INSERT_VALUE> values = new ArrayList<>();

        public static void main(String[] args) {
            SQLFactory.INSERT insTest = new SQLFactory.INSERT.Builder()
                    .table("laugn")
                    .value(":haha", "TEST ")
                    .value(":haha1", "TEST 1")
                    .value(":haha2", "TEST 2")
                    .value(":haha3", "TEST 3")
                    .build();

            System.out.println("Test : " + insTest.toString());

        }

        public void setTable(TABLE table) {
            this.table = table;
        }

        public void setValues(List<INSERT_VALUE> values) {
            this.values = values;
        }

        private void addColumn(COLUMN col) {
            this.columns.add(col);
        }

        private void addValue(INSERT_VALUE insertValue) {
            this.values.add(insertValue);
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(OP_INSERT).append(' ');
            b.append(table.toString());
            if (!this.columns.isEmpty()) {
                b.append('(');
                b.append(this.columns.stream().map(SQLFactory.COLUMN::getColumnForInsert).collect(
                        Collectors.joining(", ")));
                b.append(')').append(' ');
            }
            b.append("VALUES ");
            if (!this.values.isEmpty()) {
                b.append('(');
                b.append(values.stream().map(INSERT_VALUE::toString).collect(
                        Collectors.joining(", ")));
                b.append(')');
            }
            b.append(';');
            return b.toString();
        }

        public Query toQuery(EntityManager em) {
            String sql = this.toString();
            Query q = em.createQuery(sql);
            for (SQLFactory.INSERT_VALUE value : values) {
                q.setParameter(value.getParameter(), value.getValue());
            }
            return q;
        }


        public static class Builder {
            private SQLFactory.INSERT insert = new SQLFactory.INSERT();

            public Builder column(SQLFactory.COLUMN col) {
                this.insert.addColumn(col);
                return this;
            }

            public Builder column(String col) {
                this.insert.addColumn(new SQLFactory.COLUMN(col));
                return this;
            }

            public Builder table(SQLFactory.TABLE table) {
                this.insert.setTable(table);
                return this;
            }

            public Builder table(String tableName) {
                this.insert.setTable(new TABLE(tableName));
                return this;
            }

            public Builder value(String parameter, Object value) {
                this.insert.addValue(new SQLFactory.INSERT_VALUE(parameter, value));
                return this;
            }

            public SQLFactory.INSERT build() {
                return this.insert;
            }
        }
    }

    public static class INSERT_VALUE {
        private String parameter;
        private Object value;

        public INSERT_VALUE(String parameter, Object value) {
            this.parameter = parameter;
            this.value = value;
        }

        public String getParameter() {
            return parameter;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.parameter;
        }
    }

    public static class SELECT {
        private List<SQLFactory.COLUMN> columns = new ArrayList<>();
        private SQLFactory.TABLE table;
        private List<SQLFactory.CONDITIONS> multiConditions = new ArrayList<>();
        private List<SQLFactory.COLUMN> groups = new ArrayList<>();
        private List<SQLFactory.JOIN> joins = new ArrayList<>();

        public static void main(String[] args) {
            SQLFactory.SELECT selTest = new SQLFactory.SELECT.Builder()
                    .columns("haha", "haha1", "babi1")
                    .table("laughTable")
                    .conditions(SQLFactory.CONDITIONS
                                        .init("aaa", SQLOperator.EQUALS, ":aaa", "KAKAKAK")
                                        .and("aa", SQLOperator.EQUALS, ":aa", "KAKAKAKa")
                                        .or("baa", SQLOperator.EQUALS, ":aba", "KAKAKAKaaa"))
                    .conditions(LogicalOperator.AND,
                                SQLFactory.CONDITIONS
                                        .init("aaa", SQLOperator.EQUALS, ":aaa", "KAKAKAK")
                                        .and("aa", SQLOperator.EQUALS, ":aa", "KAKAKAKa")
                                        .or("baa", SQLOperator.EQUALS, ":aba", "KAKAKAKaaa"))
                    .build();

            System.out.println("Test : " + selTest.toString());
            System.out.println("Test : \n" + selTest.toPrettyString());
        }

        public void setTable(SQLFactory.TABLE table) {
            this.table = table;
        }


        public void addColumn(SQLFactory.COLUMN col) {
            this.columns.add(col);
        }

        public void addMultiConditions(CONDITIONS conditions) {
            this.multiConditions.add(conditions);
        }

        public void addJoins(
                SQLFactory.TABLE table,
                SQLFactory.JOIN_TYPE joinType,
                SQLFactory.COLUMN joinColumn,
                SQLOperator joinOperator,
                SQLFactory.COLUMN targetColumn
        ) {
            joins.add(new SQLFactory.JOIN(
                    table,
                    joinType,
                    joinColumn,
                    joinOperator,
                    targetColumn
            ));
        }

        public void addGroup(SQLFactory.COLUMN col) {
            this.groups.add(col);
        }

        public void addGroup(String col) {
            this.groups.add(new SQLFactory.COLUMN(col));
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.OP_SELECT).append(' ');
            b.append(this.columns.stream().map(COLUMN::toString).collect(
                    Collectors.joining(", "))).append(' ');
            b.append("FROM").append(' ').append(this.table.toString());
            if (!this.multiConditions.isEmpty()) {
                b.append("WHERE").append(' ');
                for (int i = 0; i < multiConditions.size(); i++) {
                    if (i != 0) {
                        b.append(' ').append(
                                multiConditions.get(i).getCombineWithPrevious().toString()).append(
                                ' ');
                    }
                    b.append(multiConditions.get(i).toString());
                }
            }
            if (!this.joins.isEmpty()) {
                for (JOIN join : joins) {
                    b.append(' ');
                    b.append(join.toString());
                }
            }
            if (!this.groups.isEmpty()) {
                b.append(' ').append("GROUP BY").append(' ');
                b.append(this.groups.stream().map(SQLFactory.COLUMN::getColumnForGroup).collect(
                        Collectors.joining(", ")));
            }
            return b.append(';').toString();
        }

        public Query toQuery(EntityManager em) {
            String sql = this.toString();
            this.replaceExpressionCondition(sql);
            Query q = em.createNativeQuery(sql);
            for (CONDITIONS conds : this.multiConditions) {
                for (int j = 0; j < conds.getConditionList().size(); j++) {
                    CONDITION cond = conds.getConditionList().get(j);
                    if (cond.isExpression) {
                        for (Map.Entry<String, Object> map : cond.getExpressionParameterValue().entrySet()) {
                            q.setParameter(map.getKey(), map.getValue());
                        }
                    } else {
                        q.setParameter(cond.getParameter(), cond.getValue());
                    }
                }
            }
            return q;
        }

        private void replaceExpressionCondition(String sql) {
            for (CONDITIONS conds : this.multiConditions) {
                for (int j = 0; j < conds.getConditionList().size(); j++) {
                    CONDITION cond = conds.getConditionList().get(j);
                    if (cond.isExpression) {
                        sql = sql.replace(cond.getParameter(), cond.getValue().toString());
                    }
                }
            }
        }

        public String toPrettyString() {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.OP_SELECT).append(' ');
            b.append(this.columns.stream().map(COLUMN::toString).collect(
                    Collectors.joining(","))).append(' ');
            b.append("\nFROM").append("\n\t").append(this.table.toString());
            if (!this.multiConditions.isEmpty()) {
                b.append("\nWHERE");
                for (int i = 0; i < multiConditions.size(); i++) {
                    if (i != 0) {
                        b.append(' ').append(
                                multiConditions.get(i).getCombineWithPrevious().toString()).append(
                                ' ');
                    }
                    b.append("\n\t").append(multiConditions.get(i).toString());
                }
            }
            if (!this.groups.isEmpty()) {
                b.append('\n').append("GROUP BY").append(' ');
                b.append('\n').append(
                        this.groups.stream().map(SQLFactory.COLUMN::getColumnForGroup).map(
                                ele -> '\t' + ele).collect(Collectors.joining(",\n")));
            }
            return b.append(';').toString();
        }

        public static class Builder {
            private SQLFactory.SELECT sel = new SQLFactory.SELECT();

            private static class JOIN_STATEMENT {

            }

            public Builder column(SQLFactory.COLUMN col) {
                this.sel.addColumn(col);
                return this;
            }

            public Builder column(String col) {
                this.sel.addColumn(new SQLFactory.COLUMN(col));
                return this;
            }

            public Builder columns(String... cols) {
                for (String col : cols) {
                    this.sel.addColumn(new COLUMN(col));
                }
                return this;
            }

            public Builder table(SQLFactory.TABLE table) {
                this.sel.setTable(table);
                return this;
            }

            public Builder table(String tableName) {
                this.sel.setTable(new TABLE(tableName));
                return this;
            }

            //TODO IMPLEMENT
            public Builder join(String tableName) {
                return null;
            }

            public Builder conditions(SQLFactory.CONDITIONS conditions) {
                this.sel.addMultiConditions(conditions);
                return this;
            }


            public Builder conditions(SQLFactory.CONDITION... conditions) {
                this.sel.addMultiConditions(SQLFactory.createConditions(
                        List.of(conditions),
                        null
                ));
                return this;
            }


            public Builder conditions(
                    LogicalOperator combineWithPrevious, SQLFactory.CONDITIONS conditions) {
                conditions.setCombineWithPrevious(combineWithPrevious);
                this.sel.addMultiConditions(conditions);
                return this;
            }

            public Builder group(SQLFactory.COLUMN col) {
                this.sel.addGroup(col);
                return this;
            }

            public Builder group(String col) {
                this.sel.addGroup(col);
                return this;
            }

            public SQLFactory.SELECT build() {
                return this.sel;
            }
        }
    }

    public static class UPDATE {
        private List<SQLFactory.UPDATE_COLUMN> columns = new ArrayList<>();
        private TABLE table;
        private List<SQLFactory.CONDITIONS> multiConditions = new ArrayList<>();

        public static void main(String[] args) {
            SQLFactory.UPDATE updStatement = new SQLFactory.UPDATE.Builder()
                    .column(new SQLFactory.UPDATE_COLUMN("Haha", Pair.of(":anjai", "Anjing")))
                    .column(new SQLFactory.UPDATE_COLUMN("Haha", Pair.of(":anjai", "Anjing")))
                    .table(new SQLFactory.TABLE("Benchod"))
                    .conditions(SQLFactory.CONDITIONS
                                        .init("Haha", SQLOperator.EQUALS, "BABI", "KAKAKA")
                                        .or("Haha", SQLOperator.EQUALS, "BABI", "KAKAKA"))
                    .build();
            System.out.println("Test : " + updStatement.toString());

        }

        public void setTable(TABLE table) {
            this.table = table;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.OP_UPDATE).append(' ');
            b.append(this.table.toString());
            b.append("SET").append(' ');
            b.append(this.columns.stream().map(SQLFactory.UPDATE_COLUMN::toString).collect(
                    Collectors.joining(", ")));
            if (!this.multiConditions.isEmpty()) {
                b.append(' ').append("WHERE").append(' ');
                for (int i = 0; i < multiConditions.size(); i++) {
                    if (i != 0) {
                        b.append(' ').append(
                                multiConditions.get(i).getCombineWithPrevious().toString()).append(
                                ' ');
                    }
                    b.append(multiConditions.get(i).toString());
                }
            } else {
                LOGGER.warn("WARNING, UPDATING { {}} WITHOUT ANY CONDITIONS", this.table);
            }
            return b.append(';').toString();
        }

        public void addMultiConditions(CONDITIONS conditions) {
            this.multiConditions.add(conditions);
        }

        private void addColumn(UPDATE_COLUMN updCol) {
            this.columns.add(updCol);
        }

        public static class Builder {
            private SQLFactory.UPDATE upd = new SQLFactory.UPDATE();

            public Builder column(SQLFactory.UPDATE_COLUMN updCol) {
                this.upd.addColumn(updCol);
                return this;
            }

            public Builder table(SQLFactory.TABLE table) {
                this.upd.setTable(table);
                return this;

            }

            public Builder conditions(SQLFactory.CONDITIONS conditions) {
                this.upd.addMultiConditions(conditions);
                return this;
            }

            public SQLFactory.UPDATE build() {
                return this.upd;
            }
        }


    }

    public static class UPDATE_COLUMN {
        Pair<String, Object> parameter_Value;
        private COLUMN col;

        public UPDATE_COLUMN(COLUMN col, Pair<String, Object> parameter_Value) {
            this.col = col;
            this.parameter_Value = parameter_Value;
        }

        public UPDATE_COLUMN(String col, Pair<String, Object> parameter_Value) {
            this.col = new SQLFactory.COLUMN(col);
            this.parameter_Value = parameter_Value;
        }

        public COLUMN getCol() {
            return col;
        }

        public void setCol(COLUMN col) {
            this.col = col;
        }

        public Pair<String, Object> getParameter_Value() {
            return parameter_Value;
        }

        public void setParameter_Value(
                Pair<String, Object> parameter_Value
        ) {
            this.parameter_Value = parameter_Value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(this.col.toString());
            b.append(' ').append('=').append(' ');
            b.append(this.parameter_Value.getLeft());
            return b.toString();
        }
    }

    public static class DELETE {
        private SQLFactory.TABLE table;
        private List<CONDITIONS> multiConditions = new ArrayList<>();

        public static void main(String[] args) {
            SQLFactory.DELETE del = new SQLFactory.DELETE.Builder()
                    .table("HOHO")
                    .conditions(SQLFactory.createConditions(
                            List.of(
                                    SQLFactory.createCondition("hihi", SQLOperator.EQUALS, ":hihi",
                                                               "Nah Fuck That",
                                                               LogicalOperator.OR)),
                            LogicalOperator.AND))
                    .build();
            System.out.println("Test : " + del.toString());
        }

        public TABLE getTable() {
            return table;
        }

        public void setTable(TABLE table) {
            this.table = table;
        }

        public List<CONDITIONS> getMultiConditions() {
            return multiConditions;
        }

        public void setMultiConditions(
                List<CONDITIONS> multiConditions
        ) {
            this.multiConditions = multiConditions;
        }

        public void addMultiConditions(CONDITIONS conditions) {
            this.multiConditions.add(conditions);
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(SQLFactory.OP_DELETE).append(' ');
            b.append("FROM").append(' ');
            b.append(this.table.toString());
            if (!this.multiConditions.isEmpty()) {
                b.append("WHERE").append(' ');
                for (int i = 0; i < multiConditions.size(); i++) {
                    if (i != 0) {
                        b.append(' ').append(
                                multiConditions.get(i).getCombineWithPrevious().toString()).append(
                                ' ');
                    }
                    b.append(multiConditions.get(i).toString());
                }
            }
            return b.append(';').toString();
        }

        public static class Builder {
            private SQLFactory.DELETE del = new SQLFactory.DELETE();

            public Builder table(SQLFactory.TABLE table) {
                this.del.setTable(table);
                return this;
            }

            public Builder table(String table) {
                this.del.setTable(new SQLFactory.TABLE(table));
                return this;
            }

            public Builder conditions(SQLFactory.CONDITIONS conditions) {
                this.del.addMultiConditions(conditions);
                return this;
            }

            public SQLFactory.DELETE build() {
                return this.del;
            }
        }
    }

    public static class COLUMN {
        private String alias;
        private String columnName;
        private String as;

        public COLUMN(@Nullable String alias, String columnName, @Nullable String as) {
            this.alias = alias;
            this.columnName = columnName;
            this.as = as;
        }

        public COLUMN(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnForGroup() {
            if (this.as != null) return this.as;
            return this.columnName;
        }

        public String getColumnForInsert() {
            StringBuilder b = new StringBuilder();
            if (!StringUtility.isBlank(this.alias)) b.append(as).append('.');
            b.append(this.columnName);
            return b.toString();
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (!StringUtility.isBlank(this.alias)) b.append(as).append('.');
            b.append(this.columnName);
            if (!StringUtility.isBlank(as)) b.append("AS ").append(as);
            return b.toString();
        }
    }

    public static class CONDITION {
        private String column;
        private SQLOperator op;
        private String parameter;
        private Object value;
        private LogicalOperator combineWithPrevious;
        private boolean isExpression;
        private Map<String, Object> expressionParameterValue = new HashMap<>();

        public CONDITION(
                String column, SQLOperator op, String parameter, Object value,
                LogicalOperator combineWithPrevious
        ) {
            this.column = column;
            this.op = op;
            this.parameter = parameter;
            this.value = value;
            this.combineWithPrevious = combineWithPrevious;
            this.isExpression = false;
        }

        public CONDITION(
                String column, SQLOperator op, String parameter, Object value,
                LogicalOperator combineWithPrevious, boolean isExpression,
                Map<String, Object> expressionParameterValue
        ) {
            this.column = column;
            this.op = op;
            this.parameter = parameter;
            this.value = value;
            this.combineWithPrevious = combineWithPrevious;
            this.isExpression = isExpression;
            this.expressionParameterValue = expressionParameterValue;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public SQLOperator getOp() {
            return op;
        }

        public void setOp(SQLOperator op) {
            this.op = op;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public LogicalOperator getCombineWithPrevious() {
            return combineWithPrevious;
        }

        public void setCombineWithPrevious(LogicalOperator combineWithPrevious) {
            this.combineWithPrevious = combineWithPrevious;
        }

        public boolean isExpression() {
            return isExpression;
        }

        public void setExpression(boolean expression) {
            isExpression = expression;
        }

        public Map<String, Object> getExpressionParameterValue() {
            return expressionParameterValue;
        }

        public void setExpressionParameterValue(
                Map<String, Object> expressionParameterValue
        ) {
            this.expressionParameterValue = expressionParameterValue;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(column).append(' ');
            b.append(op.getSymbol()).append(' ');
            b.append(parameter);
            return b.toString();
        }
    }

    public static class CONDITIONS {
        private List<SQLFactory.CONDITION> conditionList;
        private LogicalOperator combineWithPrevious;


        public CONDITIONS(List<CONDITION> conditionList, LogicalOperator combineWithPrevious) {
            this.conditionList = conditionList;
            this.combineWithPrevious = combineWithPrevious;
        }

        public CONDITIONS(List<CONDITION> conditionList) {
            this.conditionList = conditionList;
        }

        public CONDITIONS() {
            conditionList = new ArrayList<>();
        }

        public static SQLFactory.CONDITIONS init(
                String col, SQLOperator op, String parameter, Object value) {
            SQLFactory.CONDITIONS conditions = new SQLFactory.CONDITIONS();
            conditions.conditionList = new ArrayList<>();
            conditions.conditionList.add(new CONDITION(col, op, parameter, value, null));
            return conditions;
        }

        public SQLFactory.CONDITIONS and(
                String col, SQLOperator op, String parameter, Object value) {
            conditionList.add(new CONDITION(col, op, parameter, value, LogicalOperator.AND));
            return this;
        }

        public SQLFactory.CONDITIONS or(
                String col, SQLOperator op, String parameter, Object value) {
            conditionList.add(new CONDITION(col, op, parameter, value, LogicalOperator.OR));
            return this;
        }


        public List<CONDITION> getConditionList() {
            return conditionList;
        }

        public LogicalOperator getCombineWithPrevious() {
            return combineWithPrevious;
        }

        public void setCombineWithPrevious(LogicalOperator combineWithPrevious) {
            this.combineWithPrevious = combineWithPrevious;
        }

        @Override
        public String toString() {
            if (this.conditionList.isEmpty()) return "";
            StringBuilder b = new StringBuilder("(");
            for (int i = 0; i < this.conditionList.size(); i++) {
                SQLFactory.CONDITION cond = this.conditionList.get(i);
                if (i != 0)
                    b.append(' ').append(cond.getCombineWithPrevious().toString()).append(' ');
                b.append(cond.toString());
            }
            b.append(")");
            return b.toString();
        }
    }

    public static class JOIN {

        private TABLE joinTable;
        private SQLFactory.JOIN_TYPE joinType;
        private COLUMN joinColumn;
        private SQLOperator joinOperator;
        private COLUMN targetColumn;
        //TODO REVAMP

        public JOIN(
                TABLE joinTable, SQLFactory.JOIN_TYPE joinType, COLUMN joinColumn,
                SQLOperator joinOperator,
                COLUMN targetColumn
        ) {
            this.joinTable = joinTable;
            this.joinType = joinType;
            this.joinColumn = joinColumn;
            this.joinOperator = joinOperator;
            this.targetColumn = targetColumn;
        }


        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(joinType.toString()).append(" JOIN");
            b.append(joinTable.toString());
            b.append(" ON ").append(joinColumn.toString()).append(' ').append(
                    joinOperator.getSymbol()).append(' ');
            b.append(targetColumn.toString());
            return b.toString();
        }
    }

    public static class TABLE {
        private String alias;
        private String tableName;

        public TABLE(String alias, String tableName) {
            this.alias = alias;
            this.tableName = tableName;
        }

        public TABLE(String tableName) {
            this.tableName = tableName;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(this.tableName).append(' ');
            if (!StringUtility.isBlank(this.alias)) b.append(this.alias).append(' ');
            return b.toString();
        }
    }
}
