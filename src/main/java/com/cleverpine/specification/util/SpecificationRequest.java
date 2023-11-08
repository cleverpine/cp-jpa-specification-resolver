package com.cleverpine.specification.util;

import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.OrderByItem;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * The {@link SpecificationRequest} class is used to store the filter and sort criteria for a specification query production. It contains a
 * filter parameter, which can be a single string or a list of strings, and a list of filter items that specify how the filter should be applied.
 * It also contains a sort parameter, which can be a single string or a list of strings, and a list of sort items that specify how the
 * results should be sorted.
 *
 * @param <T> the type of the entity being queried
 */
@RequiredArgsConstructor
@Builder(setterPrefix = "with")
@Getter
public class SpecificationRequest<T> {

    private final String filterParam;

    private final List<String> filterParams;

    private final List<FilterItem<T>> filterItems;

    private final String sortParam;

    private final List<String> sortParams;

    private final List<OrderByItem<T>> sortItems;

    /**
     * Creates an empty {@link SpecificationRequest} instance.
     *
     * @param <T> the type of the entity being queried
     * @return an empty SpecificationRequest instance.
     */
    public static <T> SpecificationRequest<T> createEmpty() {
        return SpecificationRequest.<T>builder()
            .build();
    }
}
