package com.cleverpine.specification.item;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An abstract class representing a filter item, which consists of an attribute and an operator.
 * Subclasses should implement the {@link #createSpecification(QueryContext queryContext, ValueConverter valueConverter)} method to create a custom Specification for the given filter item.
 *
 * @param <T> the type of the entity
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public abstract class FilterItem<T> {

    @NonNull
    private final String attribute;

    @NonNull
    private final FilterOperator operator;

    public String getAttribute() {
        return attribute;
    }

    /**
     * Create a custom Specification for the filter item.
     *
     * @param queryContext   The QueryContext to use for the specification.
     * @param valueConverter The ValueConverter to use for the specification.
     * @return A JPA Specification for the filter item.
     */
    public abstract CpSpecification<T> createSpecification(QueryContext<T> queryContext, ValueConverter valueConverter);

}
