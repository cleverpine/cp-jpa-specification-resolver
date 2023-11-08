package com.cleverpine.specification.item;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.cleverpine.specification.util.FilterConstants.INVALID_SPECIFICATION_CREATION;

/**
 * An implementation of {@link FilterItem} that represents a filter with multiple values.
 *
 * @param <T> the type of the entity
 */
public class MultiFilterItem<T> extends FilterItem<T> {

    private final List<String> values;

    /**
     * Constructs a new {@link MultiFilterItem} instance with the specified attribute, operator, and values.
     *
     * @param attribute the name of the attribute to filter on
     * @param operator  the operator to use for the filter
     * @param values    the list of values to filter by
     * @throws NullPointerException if the {@code attribute} or {@code values} parameter is {@code null}
     */
    public MultiFilterItem(String attribute, FilterOperator operator, @NonNull List<String> values) {
        super(attribute, operator);
        this.values = values;
    }

    /**
     * Creates a new specification based on specification type.
     *
     * @param queryContext   the query context to use for the specification
     * @param valueConverter the value converter to use for the specification
     * @return a new {@link CpSpecification} instance based on this filter item
     * @throws IllegalSpecificationException if the specification cannot be created
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public CpSpecification<T> createSpecification(QueryContext<T> queryContext, ValueConverter valueConverter) {
        Class<? extends CpSpecification> specificationType = getOperator().getSpecificationType();
        try {
            return (CpSpecification<T>) specificationType
                .getDeclaredConstructor(String.class, List.class, QueryContext.class, ValueConverter.class)
                .newInstance(getAttribute(), values, queryContext, valueConverter);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalSpecificationException(
                String.format(INVALID_SPECIFICATION_CREATION, specificationType.getSimpleName()));
        }
    }

}
