package com.astar.spring.library.enums;

public enum SQLOperator {

    EQUALS("=", "= %s"),
    NOT_EQUALS("!=", "!= %s"),
    NOT_EQUALS_ALT("<>", "<> %s"),
    GREATER_THAN(">", "> %s"),
    LESS_THAN("<", "< %s"),
    GREATER_THAN_OR_EQUAL(">=", ">= %s"),
    LESS_THAN_OR_EQUAL("<=", "<= %s"),
    BETWEEN("BETWEEN", "BETWEEN(%s,%s)"),
    NOT_BETWEEN("NOT BETWEEN", "NOT BETWEEN(%s,%s)"),
    IN("IN", "IN %s"),
    NOT_IN("NOT IN", "NOT IN %s"),
    IS_NULL("IS NULL", "IS NULL"),
    IS_NOT_NULL("IS NOT NULL", "IS NOT NULL"),
    IS_DISTINCT_FROM("IS DISTINCT FROM", "IS DISTINCT FROM %s"),
    IS_NOT_DISTINCT_FROM("IS NOT DISTINCT FROM", "IS NOT DISTINCT FROM %s"),


    LIKE("LIKE", "LIKE %s"),
    NOT_LIKE("NOT LIKE", "NOT LIKE %s"),
    ILIKE("ILIKE", "ILIKE %s"),
    NOT_ILIKE("NOT ILIKE", "NOT ILIKE %s"),
    SIMILAR_TO("SIMILAR TO", "SIMILAR TO %s"),
    NOT_SIMILAR_TO("NOT SIMILAR TO", "NOT SIMILAR TO %s"),
    REGEXP_MATCH("~", "~ %s"),
    REGEXP_NOT_MATCH("!~", "!~ %s"),
    REGEXP_MATCH_CASE_INSENSITIVE("~*", null),
    REGEXP_NOT_MATCH_CASE_INSENSITIVE("!~*", null),


    JSON_FIELD_EXISTS("?", null),
    JSON_CONTAINS_ANY("?|", null),
    JSON_CONTAINS_ALL("?&",null),
    JSONB_CONTAINS("@>", null),
    JSONB_CONTAINED_BY("<@", null),


    ARRAY_OVERLAPS("&&", null),
    ARRAY_CONTAINS("&&", null),


    TEXT_SEARCH_MATCH("@@", null),


    RANGE_CONTAINS("@>", null),
    RANGE_CONTAINED_BY("<@", null),
    RANGE_OVERLAPS("&&", null),


    CIDR_CONTAINS(">>", null),
    CIDR_CONTAINED_BY("<<", null),
    IP_OVERLAP("&&", null);

    private final String symbol;
    private final String parameterizedStringFormat;

    SQLOperator(String symbol, String parameterizedStringFormat ) {
        this.symbol = symbol;
        this.parameterizedStringFormat = parameterizedStringFormat;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getParameterizedStringFormat() {
        return parameterizedStringFormat;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
