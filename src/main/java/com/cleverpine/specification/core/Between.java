package com.cleverpine.specification.core;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.INVALID_VALUES_COUNT;

/**
 * The {@link Between} class represents a multi-value specification that checks whether a given property falls within a specified
 * range of values. It extends the {@link MultiValueSpecification} abstract class and implements the
 * {@link #toPredicate(Root, CriteriaQuery, CriteriaBuilder)} method to generate a predicate for the criteria API.
 *
 * @param <T> the type of the root entity
 */
public class Between<T> extends MultiValueSpecification<T> {

    private static final Integer BETWEEN_VALUES_COUNT = 2;

    /**
     * Constructs a new specification with the given attribute path, values, query context, and value converter.
     *
     * @param attributePath  the path of the property to filter on
     * @param values         the list of two values to filter between
     * @param queryContext   the query context to use for the specification that
     * @param valueConverter the value converter to use for converting values to the appropriate types
     */
    public Between(String attributePath, List<String> values, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(attributePath, values, queryContext, valueConverter);
    }

    /**
     * Generates a predicate for the criteria API to check whether a given property falls within a specified range of values.
     *
     * @param root            the root entity
     * @param query           the criteria query
     * @param criteriaBuilder the criteria builder
     * @return the generated predicate
     * @throws InvalidSpecificationException if the list of values is null or not exactly two values
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<String> values = getValues();
        if (Objects.isNull(values) || values.size() != BETWEEN_VALUES_COUNT) {
            throw new InvalidSpecificationException(
                String.format(INVALID_VALUES_COUNT, this.getClass().getSimpleName(), BETWEEN_VALUES_COUNT));
        }

        Expression<? extends Comparable<Object>> criteriaExpression = buildCriteriaExpression(root, criteriaBuilder);
        Class<? extends Comparable<Object>> propertyType = criteriaExpression.getJavaType();

        Comparable<Object> firstValue = getValueConverter().convertToComparable(propertyType, values.get(0));
        Comparable<Object> secondValue = getValueConverter().convertToComparable(propertyType, values.get(1));
        return criteriaBuilder.between(criteriaExpression, firstValue, secondValue);
    }

}
