package com.astar.spring.library.enums;

public enum SQLOperator {

    EQUALS("="),
    NOT_EQUALS("!="),
    NOT_EQUALS_ALT("<>"),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN_OR_EQUAL("<="),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    IN("IN"),
    NOT_IN("NOT IN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    IS_DISTINCT_FROM("IS DISTINCT FROM"),
    IS_NOT_DISTINCT_FROM("IS NOT DISTINCT FROM"),


    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    ILIKE("ILIKE"),
    NOT_ILIKE("NOT ILIKE"),
    SIMILAR_TO("SIMILAR TO"),
    NOT_SIMILAR_TO("NOT SIMILAR TO"),
    REGEXP_MATCH("~"),
    REGEXP_NOT_MATCH("!~"),
    REGEXP_MATCH_CASE_INSENSITIVE("~*"),
    REGEXP_NOT_MATCH_CASE_INSENSITIVE("!~*"),


    JSON_FIELD_EXISTS("?"),
    JSON_CONTAINS_ANY("?|"),
    JSON_CONTAINS_ALL("?&"),
    JSONB_CONTAINS("@>"),
    JSONB_CONTAINED_BY("<@"),


    ARRAY_CONTAINS("@>"),
    ARRAY_CONTAINED_BY("<@"),
    ARRAY_OVERLAPS("&&"),


    TEXT_SEARCH_MATCH("@@"),


    RANGE_CONTAINS("@>"),
    RANGE_CONTAINED_BY("<@"),
    RANGE_OVERLAPS("&&"),


    CIDR_CONTAINS(">>"),
    CIDR_CONTAINED_BY("<<"),
    IP_OVERLAP("&&");

    private final String symbol;

    SQLOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
