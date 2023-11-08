package com.cleverpine.specification.expression;

import com.cleverpine.specification.util.QueryContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.util.Objects;

/**
 * This class represents a specification expression that specifies an attribute path from the root entity.
 * It extends {@link SpecificationExpression} class
 *
 * @param <T> is the type of root entity
 * @param <G> is the type of attribute
 */
public class PathSpecificationExpression<T, G> extends SpecificationExpression<T, G> {

    /**
     * Constructor for creating a PathSpecificationExpression object with given attribute path and query context.
     *
     * @param attributePath the attribute path to specify.
     * @param queryContext  the query context to use.
     */
    public PathSpecificationExpression(String attributePath,
                                       QueryContext<T> queryContext) {
        super(attributePath, queryContext);
    }

    /**
     * This method produces a JPA expression for the specified attribute path. If the attribute path should be overridden
     * by the configured one from the query context, otherwise it uses the attribute path from the class field.
     *
     * @param root            the root entity.
     * @param criteriaBuilder the criteria builder to use.
     * @return the expression for the specified attribute path.
     */
    @Override
    public Expression<G> produceExpression(Root<T> root, CriteriaBuilder criteriaBuilder) {
        String pathToEntityAttribute = queryContext.getPathToEntityAttribute(attributePath);
        String fullAttributePath = Objects.nonNull(pathToEntityAttribute) ?
            pathToEntityAttribute :
            attributePath;
        return buildPathExpressionToEntityAttribute(fullAttributePath, root);
    }
}
