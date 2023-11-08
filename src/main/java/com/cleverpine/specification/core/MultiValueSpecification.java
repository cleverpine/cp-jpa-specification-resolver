package com.cleverpine.specification.core;

import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.ValueConverter;
import lombok.Getter;

import java.util.List;

/**
 * Abstract class representing a specification with multiple values to be used in a query.
 *
 * @param <T> the type of the query result
 */
@Getter
public abstract class MultiValueSpecification<T> extends ValueSpecification<T> {

    private final List<String> values;

    /**
     * Constructor for a MultiValueSpecification.
     *
     * @param attributePath  the path for the specification
     * @param values         the list of values for the specification
     * @param queryContext   the query context for the specification
     * @param valueConverter the value converter for the specification
     */
    public MultiValueSpecification(String attributePath, List<String> values, QueryContext<T> queryContext, ValueConverter valueConverter) {
        super(attributePath, queryContext, valueConverter);
        this.values = values;
    }
}
