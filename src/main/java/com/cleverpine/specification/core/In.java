package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * The {@link In} class represents a multi-value specification that checks if a property is within a list of specified values.
 * It extends the {@link MultiValueSpecification} abstract class and implements the {@link #toPredicate(Root, CriteriaQuery, CriteriaBuilder)}
 * method to generate a predicate for the criteria API.
 *
 * @param <T> the type of the root entity
 */
public class In<T> extends MultiValueSpecification<T> {

    /**
     * Constructs a new specification with the given attribute path, values, query context, and value converter.
     *
     * @param attributePath  the path of the property to filter on
     * @param values         the list of values that the property's value must be within
     * @param queryContext   the query context to use for the specification that
     * @param valueConverter the value converter to use for converting values to the appropriate types
     */
    public In(String attributePath, List<String> values, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(attributePath, values, queryContext, valueConverter);
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
        Expression<?> criteriaExpression = buildCriteriaExpression(root, criteriaBuilder);
        Class<?> propertyType = criteriaExpression.getJavaType();
        return criteriaExpression.in(getValueConverter().convert(propertyType, getValues()));
    }

}
