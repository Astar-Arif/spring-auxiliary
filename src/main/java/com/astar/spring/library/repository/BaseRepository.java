package com.astar.spring.library.repository;

import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.pojo.Filter;
import com.astar.spring.library.pojo.MultiFilter;
import com.astar.spring.library.pojo.SQLFilter;
import com.astar.spring.library.utils.DatabaseUtility;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
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
public class BaseRepository<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepositoryInterface<T, ID> {
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

    // !LogicalOperator will override the SQLFilter combineWithPrevious
    private <S extends SQLFilter> Specification<T> createSpecificationHelper(
            List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = null;
        for (S filter : filters) {
            if (filter == null) continue;
            Specification<T> currSpec = null;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf)
                currSpec = DatabaseUtility.createSpecification(mf);
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                if (LogicalOperator.AND.equals(logicalOperator)) spec = spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec = spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return spec;
    }


    private <S extends SQLFilter> Specification<T> createSpecificationHelper(List<S> filters) {
        Specification<T> spec = null;
        for (S filter : filters) {
            if (filter == null) continue;
            Specification<T> currSpec = null;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf) currSpec = DatabaseUtility.createSpecification(mf);
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                LogicalOperator logicalOperator = Optional.ofNullable(filter.getCombineWithPrevious()).orElse(LogicalOperator.AND);
                if (LogicalOperator.AND.equals(logicalOperator)) spec = spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec = spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return spec;
    }

    private <S, U extends T> Root<U> applySpecificationToCriteria(
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

    protected <S extends T> TypedQuery<BigInteger> getSumQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<BigInteger> query = builder.createQuery(BigInteger.class);
        Root<S> root = this.applySpecificationToCriteria(spec, domainClass, query);
        query.select(builder.sum(root.get(column)));
        return this.entityManager.createQuery(query);
    }

    protected <S extends T> TypedQuery<Double> getAvgQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Double> query = builder.createQuery(Double.class);
        Root<S> root = this.applySpecificationToCriteria(spec, domainClass, query);
        query.select(builder.avg(root.get(column)));
        return this.entityManager.createQuery(query);

    }

    protected <S extends T> TypedQuery<BigInteger> getMinQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<BigInteger> query = builder.createQuery(BigInteger.class);
        Root<S> root = this.applySpecificationToCriteria(spec, domainClass, query);
        query.select(builder.min(root.get(column)));
        return this.entityManager.createQuery(query);
    }

    protected <S extends T> TypedQuery<BigInteger> getMaxQuery(
            @Nullable Specification<S> spec, Class<S> domainClass, String column) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<BigInteger> query = builder.createQuery(BigInteger.class);
        Root<S> root = this.applySpecificationToCriteria(spec, domainClass, query);
        query.select(builder.max(root.get(column)));
        return this.entityManager.createQuery(query);
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
    public <S extends SQLFilter> Page<T> findAll(
            List<S> filters, Pageable pageable, LogicalOperator logicalOperator) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
        return super.findAll(spec, pageable);
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

    @Override
    public <S extends SQLFilter> Optional<T> findOne(
            List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
        return super.findOne(spec);
    }

    @Override
    public <S extends SQLFilter> T findRequiredOne(S filter) throws Exception {
        Optional<T> optionalT = this.findOne(filter);
        if (optionalT.isEmpty()) throw new Exception("Throwing this stuff");
        return optionalT.get();
    }

    @Override
    public <S extends SQLFilter> T findRequiredOne(List<S> filters) throws Exception {
        Optional<T> optionalT = this.findOne(filters);
        if (optionalT.isEmpty()) throw new Exception("Throwing this stuff");
        return optionalT.get();
    }

    @Override
    public <S extends SQLFilter> T findRequiredOne(
            List<S> filters, LogicalOperator logicalOperator) throws Exception {
        Optional<T> optionalT = this.findOne(filters, logicalOperator);
        if (optionalT.isEmpty()) throw new Exception("Throwing this stuff");
        return optionalT.get();
    }

    @Override
    public <S extends SQLFilter> T findNullableOne(S filter) {
        Optional<T> optionalT = this.findOne(filter);
        return optionalT.orElse(null);
    }

    @Override
    public <S extends SQLFilter> T findNullableOne(List<S> filters) {
        Optional<T> optionalT = this.findOne(filters);
        return optionalT.orElse(null);
    }

    @Override
    public <S extends SQLFilter> T findNullableOne(
            List<S> filters, LogicalOperator logicalOperator) {
        Optional<T> optionalT = this.findOne(filters, logicalOperator);
        return optionalT.orElse(null);
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
            String column, List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
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
    public <S extends SQLFilter> Double avg(
            String column, List<S> filters,
            LogicalOperator logicalOperator
    ) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
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
    public <S extends SQLFilter> BigInteger min(
            String column, List<S> filters,
            LogicalOperator logicalOperator
    ) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
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
    public <S extends SQLFilter> BigInteger max(
            String column, List<S> filters,
            LogicalOperator logicalOperator
    ) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
        return getMaxQuery(spec, clazz, column).getSingleResult();
    }

    @Override
    public <S extends SQLFilter> long count(List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
        return super.count(spec);
    }

    @Override
    public <S extends SQLFilter> boolean exists(List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
        return super.exists(spec);
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
    public <S extends SQLFilter> long delete(List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = createSpecificationHelper(filters, logicalOperator);
        return super.delete(spec);
    }

    @Override
    @Transactional
    public <S extends SQLFilter> long update(
            List<S> filters, Map<String, Object> changes, LogicalOperator logicalOperator) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(this.clazz);
        Root<T> root = criteriaUpdate.from(this.clazz);
        Predicate predicate = createSpecificationHelper(filters, logicalOperator).toPredicate(root,
                                                                                              null,
                                                                                              criteriaBuilder);
        criteriaUpdate.where(predicate);
        return this.entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public void test() {
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
            if (databaseProductName.contains("PostgreSQL")) {
                databaseInfoQuery = "SELECT current_database(), current_user, inet_server_addr(), inet_server_port()";
                queryResult = (Object[]) entityManager.createNativeQuery(
                        databaseInfoQuery).getSingleResult();
                if (queryResult != null && queryResult.length == 4) {
                    dbName = (String) queryResult[0];
                    dbUser = (String) queryResult[1];
                    dbIpAddress = queryResult[2].toString();
                    dbPort = queryResult[3] != null ? queryResult[3].toString() : "N/A";
                }
            } else if (databaseProductName.contains("MySQL")) {
                databaseInfoQuery = "SELECT DATABASE(), CURRENT_USER(), @@hostname, @@port";
                queryResult = (Object[]) entityManager.createNativeQuery(
                        databaseInfoQuery).getSingleResult();
                if (queryResult != null && queryResult.length == 4) {
                    dbName = (String) queryResult[0];
                    dbUser = (String) queryResult[1];
                    dbIpAddress = (String) queryResult[2];
                    dbPort = queryResult[3] != null ? queryResult[3].toString() : "N/A";
                }
            } else if (databaseProductName.contains("Oracle")) {
                databaseInfoQuery = "SELECT SYS_CONTEXT('USERENV', 'DB_NAME'), USER, SYS_CONTEXT('USERENV', 'IP_ADDRESS') FROM DUAL";
                queryResult = (Object[]) entityManager.createNativeQuery(
                        databaseInfoQuery).getSingleResult();
                if (queryResult != null && queryResult.length == 3) {
                    dbName = (String) queryResult[0];
                    dbUser = (String) queryResult[1];
                    dbIpAddress = (String) queryResult[2];
                    dbPort = "N/A (Oracle port often configured externally)";
                }
            } else if (databaseProductName.contains("Microsoft SQL Server")) {
                databaseInfoQuery = "SELECT DB_NAME(), SUSER_SNAME(), CONVERT(VARCHAR, CONNECTIONPROPERTY('local_net_address')), CONVERT(VARCHAR, CONNECTIONPROPERTY('local_tcp_port'))";
                queryResult = (Object[]) entityManager.createNativeQuery(
                        databaseInfoQuery).getSingleResult();
                if (queryResult != null && queryResult.length == 4) {
                    dbName = (String) queryResult[0];
                    dbUser = (String) queryResult[1];
                    dbIpAddress = (String) queryResult[2];
                    dbPort = (String) queryResult[3];
                }
            } else if (databaseProductName.contains("H2")) {
                // H2: name, user. IP/Port often not applicable for embedded or simple setups.
                databaseInfoQuery = "SELECT DATABASE(), USER()";
                queryResult = (Object[]) entityManager.createNativeQuery(
                        databaseInfoQuery).getSingleResult();
                if (queryResult != null && queryResult.length == 2) {
                    dbName = (String) queryResult[0];
                    dbUser = (String) queryResult[1];
                    dbIpAddress = "N/A (Embedded/Local)";
                    dbPort = "N/A (Embedded/Local)";
                }
            } else {
                logger.warn(
                        "Unsupported database product for dynamic information fetching. Attempting generic query for name.");
                try {
                    dbName = (String) entityManager.createNativeQuery(
                            "SELECT current_database()").getSingleResult();
                } catch (Exception e) {
                    logger.warn("Generic current_database() query failed: {}", e.getMessage());
                }
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
