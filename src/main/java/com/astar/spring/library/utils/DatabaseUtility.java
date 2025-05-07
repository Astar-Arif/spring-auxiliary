package com.astar.spring.library.utils;

import com.astar.common.library.utils.ObjectUtility;
import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.enums.SQLOperator;
import com.astar.spring.library.pojo.Filter;
import com.astar.spring.library.pojo.MultiFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
//TODO TEST ALL THIS / ADD IMPLEMENTATION / CHECK ERRORS / IMPROVE

/**
 * The type Database utility.
 */
@SuppressWarnings("unchecked")
public abstract class DatabaseUtility {

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
     * Is entity boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
//    TODO TEST THIS
    public static boolean isEntity(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class);
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
     * Get entity metadata map.
     *
     * @param clazz the clazz
     * @return the map
     */
    public static Map<String, Object> getEntityMetadata(Class<?> clazz) {
        return null;
    }

    public static Join<?, ?> createJoins(
            CriteriaBuilder criteriaBuilder, Root<?> root, String entity) {
        return null;
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
        if (!validateFilter(filter)) return criteriaBuilder.conjunction();
        if (filter.getValue() instanceof Subquery<?>) {
//            TODO HANDLE SUBQUERY
        }
        Path<?> path = handlePath(root, filter.getField());
        return switch (filter.getOperator()) {
            case EQUALS -> criteriaBuilder.equal(path, filter.getValue());
            case NOT_EQUALS, NOT_EQUALS_ALT -> criteriaBuilder.notEqual(path, filter.getValue());
            case LIKE -> criteriaBuilder.like(path.as(String.class), (String) filter.getValue());
            case NOT_LIKE -> criteriaBuilder.not(
                    criteriaBuilder.like(path.as(String.class), (String) filter.getValue()));
            case ILIKE -> criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                               ((String) filter.getValue()).toLowerCase());
            case NOT_ILIKE -> criteriaBuilder.not(
                    criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                         ((String) filter.getValue()).toLowerCase()));
//            TODO TEST THIS
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
            //TODO COMPLETE THIS
            case REGEXP_MATCH -> criteriaBuilder.isTrue(
                    criteriaBuilder.function("REGEXP_MATCH",
                                             Boolean.class,
                                             path,
                                             criteriaBuilder.literal(filter.getValue()))
            );
            case REGEXP_NOT_MATCH -> criteriaBuilder.isFalse(
                    criteriaBuilder.function("REGEXP_NOT_MATCH",
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
//            TODO COMPLETE THIS
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
//            TODO TEST
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
                            // JSON object/array to check
                    )
            );
            case JSONB_CONTAINED_BY,
                 RANGE_CONTAINED_BY -> criteriaBuilder.isTrue(
                    criteriaBuilder.function(
                            "? <@ ?",
                            Boolean.class,
                            path,
                            criteriaBuilder.literal(filter.getValue().toString())
                            // JSON object/array to check against
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


//            TODO HANDLE FOR MORE DSA
            case IN -> path.in((Object[]) filter.getValue());
            case NOT_IN -> criteriaBuilder.not(path.in((Object[]) filter.getValue()));
            // TODO Handle this COMPARABLE
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
            default -> throw new IllegalArgumentException(
                    "Unsupported operator: " + filter.getOperator());
        };
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
                    secondVal = (T) arr[2];
                } else if (value instanceof Collection<?> collection) {
                    Iterator<?> iterator = collection.iterator();
                    firstVal = (T) iterator.next();
                    secondVal = (T) iterator.next();
                }
                Predicate predicate = criteriaBuilder.between(path.as(clazz), firstVal, secondVal);
                if (operator == SQLOperator.NOT_BETWEEN) predicate = predicate.not();
                yield predicate;
            }
            //TODO SHOULDN'T REACH
            default -> null;
        };

    }

    /**
     * Create predicates predicate.
     *
     * @param filters the filters
     * @return the predicate
     */
    public static Predicate createPredicates(
            CriteriaBuilder criteriaBuilder, Root<?> root, List<Filter> filters) {
        List<Predicate> predicates = new ArrayList<>();
        if (filters != null && !filters.isEmpty()) {
            for (Filter filter : filters) {
                Predicate predicate = createPredicate(criteriaBuilder, root, filter);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }
        } else {
            return criteriaBuilder.conjunction();
        }
        if (!predicates.isEmpty()) {
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        } else {
            return criteriaBuilder.conjunction();
        }
    }

    public static Predicate createPredicates(
            CriteriaBuilder criteriaBuilder, Root<?> root, MultiFilter multiFilter) {
        List<Predicate> predicates = new ArrayList<>();
        List<Filter> filters = multiFilter.getFilters();
        if (filters != null && !filters.isEmpty()) {
            for (Filter filter : filters) {
                Predicate predicate = createPredicate(criteriaBuilder, root, filter);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }
        } else {
            return criteriaBuilder.conjunction();
        }
        if (!predicates.isEmpty()) {
            LogicalOperator logicalOperator = multiFilter.getOperator();
            Predicate combined;
            if (logicalOperator.equals(LogicalOperator.OR))
                combined = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            else combined = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            if (multiFilter.isNegated()) combined = criteriaBuilder.not(combined);
            return combined;
        }
        return criteriaBuilder.conjunction();
    }

    /**
     * @param filters
     * @param <T>
     * @return
     */
    public static <T> Specification<T> createSpecifications(List<Filter> filters) {
        return (root, query, criteriaBuilder) -> createPredicates(criteriaBuilder, root, filters);
    }


    public static <T> Specification<T> createSpecification(MultiFilter multiFilter){
        return (root, query, criteriaBuilder) -> createPredicates(criteriaBuilder,root, multiFilter);
    }
    /**
     * @param filter
     * @param <T>
     * @return
     */
    public static <T> Specification<T> createSpecification(Filter filter) {
        return (root, query, criteriaBuilder) -> createPredicate(criteriaBuilder, root, filter);
    }

    /**
     * Predicate to string string.
     *
     * @param predicate the predicate
     * @return the string
     */
    public static String predicateToString(Predicate predicate) {
        return null;
    }

    /**
     * Filter to predicate string.
     *
     * @param filters the filters
     * @return the string
     */
    public static String filterToPredicate(List<Filter> filters) {
        return null;
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

    /**
     * @param filter
     * @return
     */
    private static boolean validateFilter(Filter filter) {
//        TODO PLAN IF OPERATOR IS IS_NULL OR IS_NOT_NULL
        return filter.getField() != null && (ObjectUtility.isMatch(filter.getOperator(),
                                                                   SQLOperator.IS_NULL,
                                                                   SQLOperator.IS_NOT_NULL) || filter.getValue() != null);
    }

    /**
     * @param root
     * @param field
     * @return
     */
    private static Path<?> handlePath(Root<?> root, String field) {
        if (field.contains(".")) {
            String[] parts = field.split("\\.");
            From<?, ?> current = root;
            for (int i = 0; i < parts.length - 1; i++) {
                current = current.join(parts[i]);
            }
            return current.get(parts[parts.length - 1]);
        }
        return root.get(field);
    }

    /**
     * @param filter
     * @return
     */
    private static Predicate helperCreateSubqueryPredicate(Filter filter) {
        return null;
    }
}