package com.cleverpine.specification.producer;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.EMPTY_FILTER_ATTRIBUTE;
import static com.cleverpine.specification.util.FilterConstants.INVALID_FILTER_ATTRIBUTE;

/**
 * A class for producing filter and order-by specifications from filter and order-by items.
 */
public class SimpleSpecificationProducer {

    /**
     * Produces a list of filter specifications for the given filter type and filter items.
     *
     * @param filterType     the type of the filter
     * @param filterItems    the filter items
     * @param queryContext   the query context
     * @param valueConverter the value converter
     * @param <T>            the type of the entity
     * @return a list of filter specifications
     */
    public <T> List<CpSpecification<T>> produceFilterSpecifications(Class<?> filterType,
                                                                    List<FilterItem<T>> filterItems,
                                                                    QueryContext<T> queryContext,
                                                                    ValueConverter valueConverter) {
        if (Objects.isNull(filterItems)) {
            return new ArrayList<>();
        }
        return filterItems.stream()
            .map(filterItem ->
                     produceSimpleSpecification(filterItem, filterType, queryContext, valueConverter))
            .collect(Collectors.toList());
    }

    /**
     * Produces a list of order-by specifications for the given filter type and order-by items.
     *
     * @param filterType   the type of the filter
     * @param orderByItems the order-by items
     * @param queryContext the query context
     * @param <T>          the type of the entity
     * @return a list of order-by specifications
     */
    public <T> List<CpSpecification<T>> produceOrderBySpecifications(Class<?> filterType,
                                                                     List<OrderByItem<T>> orderByItems,
                                                                     QueryContext<T> queryContext) {
        if (Objects.isNull(orderByItems)) {
            return new ArrayList<>();
        }
        return orderByItems.stream()
            .map(orderByItem ->
                     produceSimpleSpecification(orderByItem, filterType, queryContext))
            .collect(Collectors.toList());
    }

    private <T> CpSpecification<T> produceSimpleSpecification(FilterItem<T> filterItem, Class<?> filterType, QueryContext<T> queryContext, ValueConverter valueConverter) {
        validateFilterAttribute(filterItem.getAttribute(), filterType);
        return filterItem.createSpecification(queryContext, valueConverter);
    }

    private <T> CpSpecification<T> produceSimpleSpecification(OrderByItem<T> orderByItem, Class<?> filterType, QueryContext<T> queryContext) {
        validateFilterAttribute(orderByItem.getAttribute(), filterType);
        return orderByItem.createSpecification(queryContext);
    }

    private void validateFilterAttribute(String attribute, Class<?> filterType) {
        if (Objects.isNull(attribute)) {
            throw new InvalidSpecificationException(EMPTY_FILTER_ATTRIBUTE);
        }
        if (!getAllAttributesOf(filterType).contains(attribute)) {
            throw new InvalidSpecificationException(
                String.format(INVALID_FILTER_ATTRIBUTE, attribute, filterType.getSimpleName()));
        }
    }

    private Set<String> getAllAttributesOf(Class<?> type) {
        Set<String> fields = Arrays.stream(type.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toSet());
        if (Objects.nonNull(type.getSuperclass())) {
            fields.addAll(getAllAttributesOf(type.getSuperclass()));
        }
        return fields;
    }

}
