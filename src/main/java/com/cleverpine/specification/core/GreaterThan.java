package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * The {@link GreaterThan} class represents a single-value specification that checks whether a given property is greater than a
 * specific value. It extends the {@link SingleValueSpecification} abstract class, which means it can handle only one value
 * and implements the {@link #toPredicate(Root, CriteriaQuery, CriteriaBuilder)} method to generate a predicate for the criteria API.
 *
 * @param <T> the type of the root entity
 */
public class GreaterThan<T> extends SingleValueSpecification<T> {

    /**
     * Constructs an instance of the {@link GreaterThan} specification with the given path, value, query context, and value converter.
     *
     * @param attributePath  the path of the property to filter on
     * @param value          the value to filter by
     * @param queryContext   the query context to use for the specification that
     * @param valueConverter the value converter to use for converting values to the appropriate types
     */
    public GreaterThan(String attributePath, String value, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(attributePath, value, queryContext, valueConverter);
    }

    /**
     * Converts this specification into a JPA criteria API predicate.
     *
     * @param root            the root entity
     * @param query           the query to which the predicate is added
     * @param criteriaBuilder the builder to use for constructing the predicate
     * @return a predicate that corresponds to this specification
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Expression<? extends Comparable<Object>> criteriaExpression = buildCriteriaExpression(root, criteriaBuilder);
        Class<? extends Comparable<Object>> propertyType = criteriaExpression.getJavaType();
        return criteriaBuilder.greaterThan(criteriaExpression, getValueConverter().convertToComparable(propertyType, getValue()));
    }

}
