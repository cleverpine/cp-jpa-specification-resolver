package com.cleverpine.specification.item;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.core.OrderBySpecification;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SortDirection;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a sorting item used in a query to sort results based on a given attribute and sort direction.
 *
 * @param <T> the type of entity being queried
 */
@RequiredArgsConstructor
@Getter
public class OrderByItem<T> {

    @NonNull
    private final String attribute;

    @NonNull
    private final SortDirection direction;

    /**
     * Creates an {@link OrderBySpecification} for the attribute and sort direction of this order-by item.
     *
     * @param queryContext the context of the query being performed
     * @return an {@link OrderBySpecification} for the attribute and sort direction of this order-by item
     */
    public CpSpecification<T> createSpecification(@NonNull QueryContext<T> queryContext) {
        return new OrderBySpecification<>(getAttribute(), queryContext, direction);
    }
}
