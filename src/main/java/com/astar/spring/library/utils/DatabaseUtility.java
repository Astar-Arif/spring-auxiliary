package com.astar.spring.library.utils;

import com.astar.common.library.utils.ArrayUtility;
import com.astar.common.library.utils.ObjectUtility;
import com.astar.common.library.utils.ParserUtility;
import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.enums.SQLOperator;
import com.astar.spring.library.pojo.Filter;
import com.astar.spring.library.pojo.MultiFilter;
import com.astar.spring.library.pojo.QueryResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
//TODO TEST ALL THIS / ADD IMPLEMENTATION / CHECK ERRORS / IMPROVE / DECIDE TO SEPERATE JOIN

/**
 * The type Database utility.
 */
@SuppressWarnings("unchecked")
public abstract class DatabaseUtility {

    public static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtility.class);

    // TODO
    public static Selection<Tuple> createSelection(
            CriteriaBuilder criteriaBuilder, Map<String, String> selects, Root<?> root) {
        List<Selection<?>> selections = new ArrayList<>();
        for (Map.Entry<String, String> entry : selects.entrySet()) {
            selections.add(root.get(entry.getKey()));
        }
        return criteriaBuilder.tuple(selections.toArray(new Selection[0]));
    }

    /**
     * Create predicates predicate.
     *
     * @param filters the filters
     * @return the predicate
     */
    public static Predicate createPredicates(
            CriteriaBuilder criteriaBuilder, Root<?> root, List<Filter> filters) {
        Predicate predicates = null;
        if (filters != null && !filters.isEmpty()) {
            for (Filter filter : filters) {
                Predicate predicate = createPredicate(criteriaBuilder, root, filter);
                if (predicates == null) predicates = predicate;
                else if (LogicalOperator.OR.equals(filter.getCombineWithPrevious()))
                    predicates = criteriaBuilder.or(predicates, predicate);
                else predicates = criteriaBuilder.and(predicates, predicate);
            }
        } else {
            return criteriaBuilder.conjunction();
        }
        if (predicates != null) {
            return predicates;
        }
        return criteriaBuilder.conjunction();
    }


    public static Predicate createPredicates(
            CriteriaBuilder criteriaBuilder, Root<?> root, MultiFilter multiFilter) {
        Predicate predicates = null;
        List<Filter> filters = multiFilter.getFilters();
        if (filters != null && !filters.isEmpty()) {
            for (Filter filter : filters) {
                Predicate currPredicate = createPredicate(criteriaBuilder, root, filter);
                if (predicates == null) predicates = currPredicate;
                else if (LogicalOperator.OR.equals(filter.getCombineWithPrevious()))
                    predicates = criteriaBuilder.or(predicates, currPredicate);
                else if (LogicalOperator.AND.equals(filter.getCombineWithPrevious()))
                    predicates = criteriaBuilder.and(predicates, currPredicate);
            }
        } else {
            return criteriaBuilder.conjunction();
        }
        if (predicates != null) {
            if (multiFilter.isNegated()) predicates = criteriaBuilder.not(predicates);
            return predicates;
        }
        return criteriaBuilder.conjunction();
    }

    /**
     * Create predicate predicate.
     *
     * @param filter the filter
     * @return the predicate
     */
//    TODO TEST ALL THIS
    public static Predicate createPredicate(
            CriteriaBuilder criteriaBuilder, Root<?> root, Filter filter) {
        if (!isFilterValid(filter)) return criteriaBuilder.conjunction();
        Path<?> path = resolvePath(root, filter.getField(), filter.getJoinType());
        Predicate predicate = switch (filter.getOperator()) {
            case EQUALS -> criteriaBuilder.equal(path, filter.getValue());
            case NOT_EQUALS,
                 NOT_EQUALS_ALT -> criteriaBuilder.notEqual(path, filter.getValue());
            case LIKE -> criteriaBuilder.like(path.as(String.class), (String) filter.getValue());
            case NOT_LIKE -> criteriaBuilder.not(
                    criteriaBuilder.like(path.as(String.class), (String) filter.getValue()));
            case ILIKE -> criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                               ((String) filter.getValue()).toLowerCase());
            case NOT_ILIKE -> criteriaBuilder.not(
                    criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                         ((String) filter.getValue()).toLowerCase()));
            case SIMILAR_TO -> criteriaBuilder.isTrue(
                    criteriaBuilder.equal(
                            criteriaBuilder.function("SIMILAR TO", Boolean.class, path,
                                                     criteriaBuilder.literal(filter.getValue())),
                            true
                    )
            );
            case NOT_SIMILAR_TO -> criteriaBuilder.isTrue(
                    criteriaBuilder.equal(
                            criteriaBuilder.function("SIMILAR TO",
                                                     Boolean.class,
                                                     path,
                                                     criteriaBuilder.literal(filter.getValue())),
                            false
                    )
            );
            case REGEXP_MATCH -> criteriaBuilder.isTrue(
                    criteriaBuilder.function("REGEXP_MATCH",
                                             Boolean.class,
                                             path,
                                             criteriaBuilder.literal(filter.getValue()))
            );
            case REGEXP_MATCH_CASE_INSENSITIVE -> criteriaBuilder.isTrue(
                    criteriaBuilder.function("REGEXP_MATCH",
                                             Boolean.class,
                                             criteriaBuilder.function("LOWER", String.class, path),
                                             criteriaBuilder.literal(
                                                     ((String) filter.getValue()).toLowerCase()))
            );
            case REGEXP_NOT_MATCH_CASE_INSENSITIVE -> criteriaBuilder.isFalse(
                    criteriaBuilder.function("REGEXP_MATCH",
                                             Boolean.class,
                                             criteriaBuilder.function("LOWER", String.class, path),
                                             criteriaBuilder.literal(
                                                     ((String) filter.getValue()).toLowerCase()))
            );
            case REGEXP_NOT_MATCH -> criteriaBuilder.isFalse(
                    criteriaBuilder.function("REGEXP_NOT_MATCH",
                                             Boolean.class,
                                             path,
                                             criteriaBuilder.literal(filter.getValue()))
            );
            case IS_DISTINCT_FROM -> criteriaBuilder.isTrue(
                    criteriaBuilder.function("IS DISTINCT FROM",
                                             Boolean.class,
                                             criteriaBuilder.function("LOWER", String.class, path),
                                             criteriaBuilder.literal(
                                                     ((String) filter.getValue()).toLowerCase()))
            );
            case IS_NOT_DISTINCT_FROM -> criteriaBuilder.isFalse(
                    criteriaBuilder.function("IS NOT DISTINCT FROM",
                                             Boolean.class,
                                             criteriaBuilder.function("LOWER", String.class, path),
                                             criteriaBuilder.literal(
                                                     ((String) filter.getValue()).toLowerCase()))
            );
            case JSON_FIELD_EXISTS -> {
                Expression<Boolean> jsonFieldExists = criteriaBuilder.function(
                        "JSON_CONTAINS_PATH",
                        Boolean.class,
                        path,
                        criteriaBuilder.literal("one"),
                        criteriaBuilder.literal("$.\"" + filter.getValue() + "\"")
                );
                yield criteriaBuilder.isTrue(jsonFieldExists);
            }
            case JSON_CONTAINS_ANY -> {
                Expression<Boolean> jsonContainsAny = criteriaBuilder.function(
                        "jsonb_path_exists",
                        Boolean.class,
                        path,
                        criteriaBuilder.literal("$.?(@ == any($1))"), // JSONPath expression
                        criteriaBuilder.literal(filter.getValue().toString()) // The values to check
                );
                yield criteriaBuilder.isTrue(jsonContainsAny);
            }
            case JSON_CONTAINS_ALL -> {
                Expression<Boolean> jsonContainsAll = criteriaBuilder.function(
                        "jsonb_path_exists",
                        Boolean.class,
                        path,
                        criteriaBuilder.literal("$.?(@ == all($1))"), // JSONPath expression
                        criteriaBuilder.literal(filter.getValue().toString()) // The list of values
                );
                yield criteriaBuilder.isTrue(jsonContainsAll);
            }
            case JSONB_CONTAINS,
                 RANGE_CONTAINS -> criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                            "? @> ?",
                            Boolean.class,
                            path,
                            criteriaBuilder.literal(filter.getValue().toString())
                    )
            );
            case JSONB_CONTAINED_BY,
                 RANGE_CONTAINED_BY -> criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                            "? <@ ?",
                            Boolean.class,
                            path,
                            criteriaBuilder.literal(filter.getValue().toString())
                    )
            );

            case ARRAY_CONTAINS,
                 ARRAY_OVERLAPS,
                 IP_OVERLAP,
                 RANGE_OVERLAPS -> criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                            "? && ?",
                            Boolean.class,
                            path,
                            criteriaBuilder.literal(filter.getValue()) // Values to check
                    )
            );

            case TEXT_SEARCH_MATCH -> criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                            "ts_match_vq",
                            Boolean.class,
                            criteriaBuilder.function("to_tsvector", String.class,
                                                     criteriaBuilder.literal("english"), path),
                            criteriaBuilder.function("to_tsquery", String.class,
                                                     criteriaBuilder.literal(filter.getValue()))
                    )
            );
            case CIDR_CONTAINS -> criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                            ">>",
                            Boolean.class,
                            path, // Column with CIDR type
                            criteriaBuilder.literal(filter.getValue()) // CIDR value to check
                    )
            );

            case CIDR_CONTAINED_BY -> criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                            "<<",
                            Boolean.class,
                            path,
                            criteriaBuilder.literal(filter.getValue())
                    )
            );
            case IN -> {
                if (filter.getValue() instanceof Collection<?> collection) {
                    yield path.in(collection);
                } else if (filter.getValue() instanceof Subquery<?> subquery) {
                    yield path.in(subquery);
                }
                Object convertedValue = ParserUtility.parseTo(path.getJavaType(),
                                                              filter.getValue());
                yield path.in(convertedValue);
            }
            case NOT_IN -> {
                if (filter.getValue() instanceof Collection<?> collection) {
                    yield criteriaBuilder.not(path.in(collection));
                } else if (filter.getValue() instanceof Subquery<?> subquery) {
                    yield criteriaBuilder.not(path.in(subquery));
                }
                Object convertedValue = ParserUtility.parseTo(path.getJavaType(),
                                                              filter.getValue());
                yield criteriaBuilder.not(path.in(convertedValue));
            }
            case GREATER_THAN,
                 GREATER_THAN_OR_EQUAL,
                 LESS_THAN,
                 LESS_THAN_OR_EQUAL,
                 BETWEEN,
                 NOT_BETWEEN -> handleComparablePredicate(criteriaBuilder,
                                                          path,
                                                          filter.getValue(),
                                                          filter.getOperator(),
                                                          Comparable.class);
            case IS_NULL -> criteriaBuilder.isNull(path);
            case IS_NOT_NULL -> criteriaBuilder.isNotNull(path);
        };
        if (filter.getIsNegated()) predicate = criteriaBuilder.not(predicate);
        return predicate;
    }


    /**
     * @param filter
     * @param <T>
     * @return
     */
    public static <T> Specification<T> createSpecification(Filter filter) {
        return (root, query, criteriaBuilder) -> createPredicate(criteriaBuilder, root, filter);
    }

    public static <T> Specification<T> createSpecification(MultiFilter multiFilter) {
        return (root, query, criteriaBuilder) -> createPredicates(criteriaBuilder, root,
                                                                  multiFilter);
    }

    public static Join<?, ?> createJoins(
            CriteriaBuilder criteriaBuilder, Root<?> root, String entity) {
        return null;
    }

    public static <T> Query createNativeQuery(EntityManager em, String query, Class<T> clazz) {
        return em.createNativeQuery(query, clazz);
    }



    //    TODO MAYBE CHANGE THIS TO EXPRESSION PARAMETER IN THE criteriaBuilder
    private static <T extends Comparable<? super T>> Predicate handleComparablePredicate(
            CriteriaBuilder criteriaBuilder, Path<?> path, Object value, SQLOperator operator,
            Class<T> clazz
    ) {
//        TODO RECHECK THIS
        if (validateValue(value, operator)) throw new IllegalArgumentException("Error");
        return switch (operator) {
            case GREATER_THAN -> criteriaBuilder.greaterThan(path.as(clazz), (T) value);
            case GREATER_THAN_OR_EQUAL ->
                    criteriaBuilder.greaterThanOrEqualTo(path.as(clazz), (T) value);
            case LESS_THAN -> criteriaBuilder.lessThan(path.as(clazz), (T) value);
            case LESS_THAN_OR_EQUAL -> criteriaBuilder.lessThanOrEqualTo(path.as(clazz), (T) value);

            case BETWEEN, NOT_BETWEEN -> {
                T firstVal = null;
                T secondVal = null;
                if (value instanceof Pair<?, ?> pair) {
                    firstVal = (T) pair.getLeft();
                    secondVal = (T) pair.getRight();
                } else if (value instanceof Object[] arr) {
                    firstVal = (T) arr[0];
                    secondVal = (T) arr[1];
                } else if (value instanceof Collection<?> collection) {
                    Iterator<?> iterator = collection.iterator();
                    firstVal = (T) iterator.next();
                    secondVal = (T) iterator.next();
                }
                Predicate predicate = criteriaBuilder.between(path.as(clazz), firstVal, secondVal);
                if (operator == SQLOperator.NOT_BETWEEN) predicate = predicate.not();
                yield predicate;
            }
            //TODO SHOULD BE IMPOSSIBLE CASE
            default -> null;
        };
    }

    /**
     * @param filter
     * @return
     */
    private static Predicate handleSubqueryPredicate(Filter filter) {
        return null;
    }

    /**
     * @param filter
     * @return
     */
    public static boolean isFilterValid(Filter filter) {
//        TODO PLAN IF OPERATOR IS IS_NULL OR IS_NOT_NULL
        return filter != null && filter.getField() != null && (ObjectUtility.isMatch(
                filter.getOperator(),
                SQLOperator.IS_NULL,
                SQLOperator.IS_NOT_NULL) || filter.getValue() != null);
    }


    /**
     * @param root
     * @param field
     * @return
     */
    private static Path<?> resolvePath(Root<?> root, String field, JoinType joinType) {
        //"as.kk.customer"
        if (field.contains(".")) {
            if (joinType == null) joinType = JoinType.INNER;
            String[] parts = field.split("\\.");
            From<?, ?> current = root;
            for (int i = 0; i < parts.length - 1; i++) {
                current = current.join(parts[i], joinType);
            }
            return current.get(parts[parts.length - 1]);
        }
        return root.get(field);
    }


    private static boolean validateValue(Object value, SQLOperator operator) {
//        if value not array and length < 2 return error,
//        if value not common.lang Pair return error,
//        if value not Collections return error
        return switch (operator) {
            case GREATER_THAN,
                 GREATER_THAN_OR_EQUAL,
                 LESS_THAN,
                 LESS_THAN_OR_EQUAL,
                 NOT_BETWEEN,
                 BETWEEN -> validateComparable(value, Comparable.class, operator);
//            TODO MOVE THIS TO validateComparable
//            case NOT_BETWEEN,
//                 BETWEEN -> {
//                if (value instanceof Pair<?, ?> pair && pair.getLeft() != null && pair.getRight() != null)
//                    yield true;
//                if (value instanceof Object[] arr && arr.length > 1) yield true;
//                if (value instanceof Collection<?> collection && collection.size() > 1) yield true;
//                yield false;
//            }
            default -> throw new IllegalArgumentException("Error");
        };
    }

    //TODO COMPLETE THIS
    private static <T extends Comparable<? super T>> boolean validateComparable(
            Object value, Class<T> clazz,
            SQLOperator operator
    ) {
        return switch (operator) {
            case GREATER_THAN,
                 GREATER_THAN_OR_EQUAL,
                 LESS_THAN,
                 LESS_THAN_OR_EQUAL -> clazz.isInstance(value);
            case NOT_BETWEEN,
                 BETWEEN -> {
                if (value instanceof Pair<?, ?> pair && pair.getLeft() != null && pair.getRight() != null) {
                    yield clazz.isInstance(pair.getLeft()) && clazz.isInstance(pair.getRight());
                }
                if (value instanceof Object[] arr && arr.length > 1) {
                    yield clazz.isInstance(arr[0]) && clazz.isInstance(arr[1]);
                }
                if (value instanceof Collection<?> collection && collection.size() > 1) {
                    Iterator<?> iterator = collection.iterator();
                    if (clazz.isInstance(iterator.next()) && clazz.isInstance(iterator.next())) {
                        yield true;
                    }
                }
                yield false;
            }
            default -> false;
        };
    }

    public static void applyValuesToParameters(
            Query query,
            Map<String, Object> parameterValue
    ) {
        for (Map.Entry<String, Object> entry : parameterValue.entrySet()) {
            applyValueToParameter(query, entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param query
     * @param parameter
     * @param value
     */
    public static void applyValueToParameter(
            Query query,
            String parameter,
            Object value
    ) {
        query.setParameter(parameter, value);
    }

    public static QueryResult<?> convertToQueryResult(Object queryResult) {
        if (queryResult instanceof List<?> list) {
            return new QueryResult<>(list, 1, 1, list.size(), list.size());
        } else if (queryResult instanceof Page<?> page) {
            return new QueryResult<>(
                    page,
                    page.getNumber() + 1,
                    page.getTotalPages(),
                    page.getNumberOfElements(),
                    page.getTotalElements());
        } else {
            throw new IllegalArgumentException(
                    "Unsupported query result type: " + queryResult.getClass());
        }
    }

    public static <T> QueryResult<List<T>> sliceQueryResult(
            List<T> queryResult, int pageNumber, int elePerPage) {
        if (pageNumber < 1 || elePerPage < 1) {
            throw new IllegalArgumentException(
                    String.format("Invalid Page Number [%d] or ElePerPage [%d]", pageNumber,
                                  elePerPage));
        }
        int totalSize = queryResult.size();
        int totalPages = (int) Math.ceil((double) totalSize / elePerPage);
        // Calculate start and end indices (0-based)
        int startIndex = (pageNumber - 1) * elePerPage;
        int endIndex = Math.min(startIndex + elePerPage, totalSize);
        List<T> pageData;
        pageData = ArrayUtility.slice(queryResult, startIndex, endIndex);
        return new QueryResult<>(
                pageData,
                pageNumber,
                totalPages,
                pageData.size(),
                totalSize
        );
    }

    /**
     * Get table name string.
     *
     * @param clazz the clazz
     * @return the string
     */
    public static String getTableName(EntityManager entityManager, Class<?> clazz) {
//        TODO CREATE EXCEPTION ENUMS;
        if (!isEntity(clazz)) {
            throw new IllegalArgumentException("Not An Entity");
        }
        SessionFactoryImplementor sessionFactoryImplementor = entityManager.unwrap(
                SessionFactoryImplementor.class);
        MappingMetamodel mappingMetamodel = sessionFactoryImplementor.getMappingMetamodel();
        EntityPersister entityPersister = mappingMetamodel.findEntityDescriptor(clazz);
        if (entityPersister == null) {
            throw new IllegalArgumentException("Not An Entity");
        }
        if (entityPersister instanceof SingleTableEntityPersister) {
            return ((SingleTableEntityPersister) entityPersister).getTableName();
        }
        throw new IllegalArgumentException("Unable to find entity name");
    }

    /**
     * Get columns set.
     *
     * @param clazz the clazz
     * @return the set
     */
    public static Set<String> getColumns(EntityManager entityManager, Class<?> clazz) {
        if (!isEntity(clazz)) throw new IllegalArgumentException("Not An Entity");

        EntityType<?> entityType = entityManager.getMetamodel().entity(clazz);
        return entityType
                .getAttributes()
                .stream()
                .map(Attribute::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Get entity metadata map.
     *
     * @param clazz the clazz
     * @return the map
     */
    public static Map<String, Object> getEntityMetadata(Class<?> clazz) {
        return null;
    }

    /**
     * Has column boolean.
     *
     * @param clazz  the clazz
     * @param column the column
     * @return the boolean
     */
    public static boolean hasColumn(EntityManager entityManager, Class<?> clazz, String column) {
        Set<String> entityColumns = getColumns(entityManager, clazz);
        return entityColumns.contains(column);
    }

    /**
     * @param entityManager
     * @param clazz
     * @param column
     * @return
     */
    public boolean isColumnUnique(EntityManager entityManager, Class<?> clazz, String column) {
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<?> et = metamodel.entity(clazz);
        Attribute<?, ?> att = et.getAttribute(column);
        if (att.getJavaMember() instanceof Field field) {
            Column col = field.getAnnotation(Column.class);
            return (col != null && col.unique());
        } else {
            return false;
        }
    }

    /**
     * Is entity boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
//    TODO TEST THIS
    public static boolean isEntity(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class);
    }

    public static String toSQLString(Query query) {
        try {
            return query.unwrap(org.hibernate.query.Query.class).getQueryString();
        } catch (Exception t) {
            LOGGER.error("Fail to convert to String ", t);
            return null;
        }
    }

}