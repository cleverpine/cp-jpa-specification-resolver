package com.cleverpine.specification.core;

import com.cleverpine.specification.expression.SpecificationExpression;
import com.cleverpine.specification.expression.SpecificationExpressionFactory;
import com.cleverpine.specification.util.QueryContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A base class for specifications that involve an attribute path, which is used on the specification's expression build.
 * It implements the {@link com.cleverpine.specification.core.CpSpecification} interface and provides methods for building
 * the criteria expression of the specification.
 * This class is meant to be extended by concrete implementation of a specification that uses the attribute path in various ways, such as for
 * filtering, sorting, or grouping.
 *
 * @param <T> the root type of the queried entity
 */
@RequiredArgsConstructor
@Getter
public abstract class CriteriaExpressionSpecification<T> implements CpSpecification<T> {

    private final String attributePath;

    private final QueryContext<T> queryContext;

    /**
     * Builds the criteria expression of the specification. If the attribute path contains a nested attributes, it
     * creates a join or fetch join based on the configured join type.
     *
     * @param root            the root object of the entity
     * @param criteriaBuilder the builder used to construct the criteria
     * @param <G>             the type of the attribute
     * @return the criteria expression of this specification
     */
    protected <G> Expression<G> buildCriteriaExpression(Root<T> root, CriteriaBuilder criteriaBuilder) {
        SpecificationExpression<T, G> specificationExpression =
            SpecificationExpressionFactory.createSpecificationExpression(attributePath, queryContext);
        return specificationExpression.produceExpression(root, criteriaBuilder);
    }
}
