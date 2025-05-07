package com.astar.spring.library.repository;

import com.astar.spring.library.enums.LogicalOperator;
import com.astar.spring.library.pojo.Filter;
import com.astar.spring.library.pojo.MultiFilter;
import com.astar.spring.library.pojo.SQLFilter;
import com.astar.spring.library.utils.DatabaseUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

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

    @Override
    public <S extends SQLFilter> List<T> findAll(S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
            //TODO: HIHI
        else throw new RuntimeException("Invalid Filter");

        return super.findAll(spec);
    }

    @Override
    public <S extends SQLFilter> Page<T> findAll(
            List<S> filters, Pageable pageable, LogicalOperator logicalOperator) {
        Specification<T> spec = null;
        for (S filter : filters) {
            Specification<T> currSpec = null;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf)
                currSpec = DatabaseUtility.createSpecification(mf);
                // !Should be impossible
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                if (LogicalOperator.AND.equals(logicalOperator)) spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return super.findAll(spec, pageable);
    }

    @Override
    public <S extends SQLFilter> Optional<T> findOne(S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
            //TODO: HIHI
        else throw new RuntimeException("Invalid Filter");
        return super.findOne(spec);

    }

    @Override
    public <S extends SQLFilter> Optional<T> findOne(
            List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = null;
        for (S filter : filters) {
            Specification<T> currSpec = null;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf)
                currSpec = DatabaseUtility.createSpecification(mf);
                // !Should be impossible
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                if (LogicalOperator.AND.equals(logicalOperator)) spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return super.findOne(spec);
    }

    @Override
    public <S extends SQLFilter> T findRequiredOne(S filter) throws Exception {
        Optional<T> optionalT = this.findOne(filter);
        // TODO :  JAJA
        if (optionalT.isEmpty()) throw new Exception("Throwing this stuff");
        return optionalT.get();
    }

    @Override
    public <S extends SQLFilter> T findRequiredOne(
            List<S> filters, LogicalOperator logicalOperator) throws Exception {
        Optional<T> optionalT = this.findOne(filters, logicalOperator);
        // TODO :  JAJA
        if (optionalT.isEmpty()) throw new Exception("Throwing this stuff");
        return optionalT.get();
    }

    @Override
    public <S extends SQLFilter> T findNullableOne(S filter) {
        Optional<T> optionalT = this.findOne(filter);
        return optionalT.orElse(null);
    }

    @Override
    public <S extends SQLFilter> T findNullableOne(
            List<S> filters, LogicalOperator logicalOperator) {
        Optional<T> optionalT = this.findOne(filters, logicalOperator);
        return optionalT.orElse(null);
    }

    @Override
    public <S extends SQLFilter> long count(List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = null;
        for (S filter : filters) {
            Specification<T> currSpec = null;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf)
                currSpec = DatabaseUtility.createSpecification(mf);
                // !Should be impossible
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                if (LogicalOperator.AND.equals(logicalOperator)) spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return super.count(spec);
    }

    @Override
    public <S extends SQLFilter> boolean exists(List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = null;
        for (S filter : filters) {
            Specification<T> currSpec = null;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf)
                currSpec = DatabaseUtility.createSpecification(mf);
                // !Should be impossible
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                if (LogicalOperator.AND.equals(logicalOperator)) spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return super.exists(spec);
    }

    @Override
    @Transactional
    public <S extends SQLFilter> long delete(S filter) {
        Specification<T> spec;
        if (filter instanceof Filter f) spec = DatabaseUtility.createSpecification(f);
        else if (filter instanceof MultiFilter mf) spec = DatabaseUtility.createSpecification(mf);
            //TODO: HIHI
        else throw new RuntimeException("Invalid Filter");
        return super.delete(spec);

    }

    @Override
    @Transactional
    public <S extends SQLFilter> long delete(List<S> filters, LogicalOperator logicalOperator) {
        Specification<T> spec = null;
        for (S filter : filters) {
            Specification<T> currSpec = null;
            if (filter instanceof Filter f) currSpec = DatabaseUtility.createSpecification(f);
            else if (filter instanceof MultiFilter mf)
                currSpec = DatabaseUtility.createSpecification(mf);
                // !Should be impossible
            else throw new RuntimeException("Invalid Stuff");
            if (spec == null) spec = currSpec;
            else {
                if (LogicalOperator.AND.equals(logicalOperator)) spec.and(currSpec);
                else if (LogicalOperator.OR.equals(logicalOperator)) spec.or(currSpec);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        return super.delete(spec);
    }

    @Override
    @Transactional
    public <S extends SQLFilter> long update(
            List<S> filters, Map<String, Object> changes, LogicalOperator logicalOperator) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(this.clazz);
        Root<T> root = criteriaUpdate.from(this.clazz);
        for (Map.Entry<String, Object> entry : changes.entrySet()) {
            criteriaUpdate.set(root.get(entry.getKey()), entry.getValue());
        }
        Predicate pred = null;
        for (S filter : filters) {
            Predicate currPred = null;
            if (filter instanceof Filter f)
                currPred = DatabaseUtility.createPredicate(criteriaBuilder, root, f);
            else if (filter instanceof MultiFilter mf)
                currPred = DatabaseUtility.createPredicates(criteriaBuilder, root, mf);
                // !Should be impossible
            else throw new RuntimeException("Invalid Stuff");
            if (pred == null) pred = currPred;
            else {
                if (LogicalOperator.AND.equals(logicalOperator))
                    criteriaBuilder.and(pred, currPred);
                else if (LogicalOperator.OR.equals(logicalOperator))
                    criteriaBuilder.or(pred, currPred);
                else throw new RuntimeException("Invalid Logical Operator");
            }
        }
        criteriaUpdate.where(pred);
        return this.entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public void test() {
        try {
            String databaseName = (String) entityManager.createNativeQuery(
                    "SELECT current_database()").getSingleResult();
            System.out.println("Connected to database: " + databaseName);
        } catch (Exception e) {
            System.err.println("Error fetching database name: " + e.getMessage());
        }
    }
}
