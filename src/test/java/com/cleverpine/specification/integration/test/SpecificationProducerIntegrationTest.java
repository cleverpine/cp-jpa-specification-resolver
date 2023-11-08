package com.cleverpine.specification.integration.test;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.parser.SpecificationParserManager;
import com.cleverpine.specification.util.ValueConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;
import java.util.stream.Collectors;

public class SpecificationProducerIntegrationTest {

    private static final String PERSISTENCE_UNIT_TEST_NAME = "cp-jpa-specification-resolver-test";

    protected static EntityManager entityManager;

    protected final SpecificationParserManager specificationParserManager;

    protected final ValueConverter valueConverter;

    protected SpecificationProducerIntegrationTest(SpecificationParserManager specificationParserManager,
                                                   ValueConverter valueConverter) {
        this.specificationParserManager = specificationParserManager;
        this.valueConverter = valueConverter;
    }

    @BeforeAll
    public static void init() {
        entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_TEST_NAME)
            .createEntityManager();
    }

    protected <T> List<T> findAll(CpSpecification<T> specification, Class<T> entityType) {
        return entityManager.createQuery(getQuery(specification, entityType))
            .getResultList();
    }

    protected <T> T findOne(CpSpecification<T> specification, Class<T> entityType) {
        return entityManager.createQuery(getQuery(specification, entityType))
            .getSingleResult();
    }

    protected String createJsonArrayFilterParam(List<List<String>> filterParams) {
        String collect = filterParams.stream()
            .map(filterParamItems ->
                     String.format("[\"%s\",\"%s\",\"%s\"]",
                                   filterParamItems.get(0),
                                   filterParamItems.get(1),
                                   filterParamItems.get(2)))
            .collect(Collectors.joining(","));
        return String.format("[%s]", collect);
    }

    protected String createJsonArraySortParam(List<List<String>> sortParams) {
        String collect = sortParams.stream()
            .map(sortParamItems ->
                     String.format("[\"%s\",\"%s\"]",
                                   sortParamItems.get(0),
                                   sortParamItems.get(1)))
            .collect(Collectors.joining(","));
        return collect;
    }

    private <T> CriteriaQuery<T> getQuery(CpSpecification<T> specification, Class<T> entityType) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(entityType);
        Root<T> root = query.from(entityType);
        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
        query.where(predicate);
        return query;
    }
}
