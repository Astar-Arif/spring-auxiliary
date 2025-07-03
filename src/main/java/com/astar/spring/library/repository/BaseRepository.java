package com.astar.spring.library.repository;

import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.pojo.Filter;
import com.astar.spring.library.pojo.MultiFilter;
import com.astar.spring.library.pojo.SQLFilter;
import com.astar.spring.library.utils.DatabaseUtility;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//TODO IMPROVE
//TODO TEST
public class BaseRepository<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepositoryInterface<T,ID> {
    private final EntityManager entityManager;
    private final Class<T> clazz;
    private final Logger logger;

    public BaseRepository(
            JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.clazz = entityInformation.getJavaType();
        this.logger = LoggerFactory.getLogger(clazz);
    }


    @Override
    public List<Tuple> query(String... columns) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<T> root = criteriaQuery.from(this.clazz);
        List<Selection<?>> selections = DatabaseUtility.createSelection(criteriaBuilder, columns, root);;
        criteriaQuery.multiselect(selections);
        TypedQuery<Tuple> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public <S extends SQLFilter> List<Tuple> query(S filter, String... columns) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<T> root = criteriaQuery.from(clazz);
        List<Selection<?>> selections = DatabaseUtility.createSelection(criteriaBuilder, columns, root);
        Predicate predicate = createSpecificationHelper(filter).toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.multiselect(selections).where(predicate);
        TypedQuery<Tuple> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public <S extends SQLFilter> List<Tuple> query(List<S> filter, String... columns) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<T> root = criteriaQuery.from(clazz);
        List<Selection<?>> selections = DatabaseUtility.createSelection(criteriaBuilder, columns, root);
        Predicate predicate = createSpecificationHelper(filter).toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.multiselect(selections).where(predicate);
        TypedQuery<Tuple> query = this.entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public <D> List<D> nativeQueryList(String query, Class<D> clazz) {
        Query q = entityManager.createNativeQuery(query, clazz);
        return q.getResultList();
    }

    @Override
    public <D> D nativeQueryObject(String query, Class<D> clazz) {
        Query q = entityManager.createNativeQuery(query, clazz);
        try {
            return (D) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <D> D hibernateQueryObject(String query, Class<D> clazz) {
        TypedQuery<D>  typedQuery = this.entityManager.createQuery(query, clazz);
        return typedQuery.getSingleResult();
    }

    @Override
    public <D> List<D> hibernateQueryList(String query, Class<D> clazz) {
        TypedQuery<D> typedQuery = this.entityManager.createQuery(query, clazz);
        return typedQuery.getResultList();
    }

    protected <S extends T> TypedQuery<BigInteger> getSumQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<BigInteger> query = builder.createQuery(BigInteger.class);
        Root<S> root = this.applySpecificationToCriteriaCopy(spec, domainClass, query);
        query.select(builder.sum(root.get(column)));
        return this.entityManager.createQuery(query);
    }

    protected <S extends T> TypedQuery<Double> getAvgQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Double> query = builder.createQuery(Double.class);
        Root<S> root = this.applySpecificationToCriteriaCopy(spec, domainClass, query);
        query.select(builder.avg(root.get(column)));
        return this.entityManager.createQuery(query);

    }

    protected <S extends T> TypedQuery<BigInteger> getMinQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<BigInteger> query = builder.createQuery(BigInteger.class);
        Root<S> root = this.applySpecificationToCriteriaCopy(spec, domainClass, query);
        query.select(builder.min(root.get(column)));
        return this.entityManager.createQuery(query);
    }

    protected <S extends T> TypedQuery<BigInteger> getMaxQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<BigInteger> query = builder.createQuery(BigInteger.class);
        Root<S> root = this.applySpecificationToCriteriaCopy(spec, domainClass, query);
        query.select(builder.max(root.get(column)));
        return this.entityManager.createQuery(query);
    }

    private <S, U extends T> Root<U> applySpecificationToCriteriaCopy(
            @Nullable Specification<U> spec, Class<U> domainClass, CriteriaQuery<S> query) {
        Assert.notNull(domainClass, "Domain class must not be null");
        Assert.notNull(query, "CriteriaQuery must not be null");
        Root<U> root = query.from(domainClass);
        if (spec != null) {
            CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
            Predicate predicate = spec.toPredicate(root, query, builder);
            if (predicate != null) {
                query.where(predicate);
            }
        }
        return root;
    }


    @Override
    public <S extends SQLFilter> List<T> findAll(S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Filter");
        return super.findAll(spec);
    }

    @Override
    public <S extends SQLFilter> List<T> findAll(List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return super.findAll(spec);
    }

    @Override
    public <S extends SQLFilter> Page<T> findAll(List<S> filters, Pageable pageable) {
        Specification<T> spec = createSpecificationHelper(filters);
        return super.findAll(spec, pageable);
    }

    @Override
    public <S extends SQLFilter> List<Tuple> findAll(S filter, Map<String, String> projections) {
        return null;
    }

    @Override
    public <S extends SQLFilter> Optional<T> findOne(S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Filter");
        return super.findOne(spec);
    }

    @Override
    public <S extends SQLFilter> Optional<T> findOne(List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return super.findOne(spec);
    }

//    TODO IMPLEMENT
    @Override
    public <S extends SQLFilter> List<Tuple> findOne(S filter, Map<String, String> projections) {
        return null;
    }

    @Override
    public <S extends SQLFilter> BigInteger sum(String column, S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Filter");
        return getSumQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> BigInteger sum(
            String column, List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return getSumQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> Double avg(String column, S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Filter");
        return getAvgQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> Double avg(String column, List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return getAvgQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> BigInteger min(String column, S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Filter");
        return getMinQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> BigInteger min(String column, List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return getMinQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> BigInteger max(String column, S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Filter");
        return getMaxQuery(spec, clazz, column).getSingleResult();

    }

    @Override
    public <S extends SQLFilter> BigInteger max(String column, List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return getMaxQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> long count(List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return super.count(spec);
    }

    @Override
    public <S extends SQLFilter> boolean exists(List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return super.exists(spec);
    }

    @Override
    @Transactional
//    TODO: IS NOT SUPPORTING JOIN YET
    public <S extends SQLFilter> long update(
            List<S> filters, Map<String, Object> changes, LogicalOperator logicalOperator) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(this.clazz);
        Root<T> root = criteriaUpdate.from(this.clazz);
        for (Map.Entry<String, Object> entry : changes.entrySet()) {
            criteriaUpdate.set(root.get(entry.getKey()), entry.getValue());
        }
        Predicate predicate = createSpecificationHelper(filters).toPredicate(root,
                                                                             null,
                                                                             criteriaBuilder);
        criteriaUpdate.where(predicate);
        return this.entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    @Transactional
    public <S extends SQLFilter> long delete(S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Filter");
        return super.delete(spec);

    }

    @Override
    @Transactional
    public <S extends SQLFilter> long delete(List<S> filters) {
        Specification<T> spec = createSpecificationHelper(filters);
        return super.delete(spec);
    }

    private <S extends SQLFilter> Specification<T> createSpecificationHelper(List<S> filters) {
        Specification<T> spec = null;
        for (S filter : filters) {
            if (filter == null) continue;
            Specification<T> currSpec;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf)
                currSpec = DatabaseUtility.createSpecification(mf);
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                LogicalOperator logicalOperator = Optional.ofNullable(
                        filter.getCombineWithPrevious()).orElse(LogicalOperator.AND);
                if (LogicalOperator.AND.equals(logicalOperator)) spec = spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec = spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return Optional.ofNullable(spec).orElse(Specification.where(null));
    }

    private <S extends SQLFilter> Specification<T> createSpecificationHelper(S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
        else throw new RuntimeException("Invalid Stuff");
        return Optional.ofNullable(spec).orElse(Specification.where(null));
    }
    @Override
    public void getDatabaseInfo() {
        Connection connection = null;
        String dbName = "N/A";
        String dbVersion = "N/A";
        String dbUser = "N/A";
        String dbIpAddress = "N/A";
        String dbPort = "N/A";
        String databaseProductName = "N/A";
        try {
            Session session = entityManager.unwrap(Session.class);
            connection = session.doReturningWork(conn -> conn);

            if (connection == null) {
                logger.error("Error: Could not unwrap JDBC Connection from EntityManager.");
                return;
            }
            DatabaseMetaData metaData = connection.getMetaData();
            databaseProductName = metaData.getDatabaseProductName();
            logger.debug("Detected database product: {}", databaseProductName);
            dbVersion = metaData.getDatabaseProductVersion();
            dbUser = metaData.getUserName();
            String databaseInfoQuery = "";
            Object[] queryResult = null;
            boolean isRecognizedDatabaseProduct = true;

            Map<String, String> databaseProductQueryMap = Map.ofEntries(
                    Map.entry("PostgreSQL",
                              "SELECT current_database(), current_user, inet_server_addr(), inet_server_port();"),
                    Map.entry("MySQL", "SELECT DATABASE(), CURRENT_USER(), @@hostname, @@port;"),
                    Map.entry("Oracle",
                              "SELECT SYS_CONTEXT('USERENV', 'DB_NAME'), USER, SYS_CONTEXT('USERENV', 'IP_ADDRESS'), 'N/A (Oracle port often configured externally)' AS Port FROM DUAL;"),
                    Map.entry("Microsoft SQL Server",
                              "SELECT DB_NAME(), SUSER_SNAME(), CONVERT(VARCHAR, CONNECTIONPROPERTY('local_net_address')), CONVERT(VARCHAR, CONNECTIONPROPERTY('local_tcp_port');"),
                    Map.entry("H2", "SELECT DATABASE(), USER(), 'N/A' AS IPADDRESS, 'N/A' AS PORT;")
            );

            databaseInfoQuery = databaseProductQueryMap.get(databaseProductName);
            if (databaseInfoQuery == null) {
                isRecognizedDatabaseProduct = false;
                logger.warn(
                        "Unsupported database product for dynamic information fetching. Attempting generic query for name.");

            }

            if (isRecognizedDatabaseProduct) {
                queryResult = (Object[]) entityManager.createNativeQuery(
                        databaseInfoQuery).getSingleResult();
                if (queryResult != null) {
                    dbName = (String) queryResult[0];
                    dbUser = (String) queryResult[1];
                    dbIpAddress = queryResult[2].toString();
                    dbPort = queryResult[3] != null ? queryResult[3].toString() : "N/A";
                }
            } else {
                dbName = (String) entityManager.createNativeQuery(
                        "SELECT current_database()").getSingleResult();
            }

        } catch (SQLException e) {
            logger.error("SQL Exception while trying to get database metadata or execute query: {}",
                         e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error fetching database details dynamically: {}", e.getMessage(), e);
        }

        logger.info("  Database Product  : {}", databaseProductName);
        logger.info("  Database Name     : {}", dbName);
        logger.info("  Product Version   : {}", dbVersion);
        logger.info("  Current User      : {}", dbUser);
        logger.info("  Server IP Address : {}", dbIpAddress);
        logger.info("  Server Port       : {}", dbPort);
    }
}
