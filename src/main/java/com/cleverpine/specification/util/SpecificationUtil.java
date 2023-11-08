package com.cleverpine.specification.util;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.item.SingleFilterItem;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.cleverpine.specification.util.FilterConstants.EMPTY_STRING;
import static com.cleverpine.specification.util.FilterConstants.ENTITY_ATTRIBUTE_SEPARATOR;
import static com.cleverpine.specification.util.FilterConstants.INVALID_FILTER_ARGS_COUNT;
import static com.cleverpine.specification.util.FilterConstants.INVALID_FILTER_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.INVALID_SORT_ARGS_COUNT;
import static com.cleverpine.specification.util.FilterConstants.INVALID_SORT_DIRECTION;
import static com.cleverpine.specification.util.FilterConstants.VALID_FILTER_ARGS_COUNT;
import static com.cleverpine.specification.util.FilterConstants.VALID_SORT_ARGS_COUNT;

/**
 * Utility class used in the Specification producing.
 */
public final class SpecificationUtil {

    /**
     * Builds the full path to an entity attribute.
     *
     * @param args the parts of the path
     * @return the full path as a string
     */
    public static String buildFullPathToEntityAttribute(String... args) {
        if (Objects.isNull(args)) {
            return EMPTY_STRING;
        }
        return String.join(ENTITY_ATTRIBUTE_SEPARATOR, args);
    }

    /**
     * Gets the {@link FilterOperator} by a filter value.
     *
     * @param operatorValue the symbol of the filter operator
     * @return the found {@link FilterOperator}
     * @throws InvalidSpecificationException if the filer operator is not found
     */
    public static FilterOperator getFilterOperatorByValue(String operatorValue) {
        return FilterOperator.getByValue(operatorValue)
            .orElseThrow(() -> new InvalidSpecificationException(String.format(INVALID_FILTER_OPERATOR, operatorValue)));
    }

    /**
     * Gets the {@link SortDirection} by a sort value.
     *
     * @param value the symbol of the sort direction
     * @return the found {@link SortDirection}
     * @throws InvalidSpecificationException if the sort direction is not found
     */
    public static SortDirection getSortDirectionByValue(String value) {
        try {
            return SortDirection.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidSpecificationException(String.format(INVALID_SORT_DIRECTION, value.toLowerCase()));
        }
    }

    /**
     * Creates an {@link OrderByItem} based on the given sort arguments.
     *
     * @param sortArgs the list of sort arguments
     * @return the created {@link OrderByItem}
     * @throws InvalidSpecificationException if the sort arguments are invalid
     */
    public static <T> OrderByItem<T> createSortItem(List<String> sortArgs) {
        if (!SpecificationUtil.isSortItemValid(sortArgs)) {
            throw new InvalidSpecificationException(
                String.format(INVALID_SORT_ARGS_COUNT, VALID_SORT_ARGS_COUNT));
        }

        String sortAttribute = sortArgs.get(0);
        String value = sortArgs.get(1).toUpperCase();

        SortDirection sortDirection = SpecificationUtil.getSortDirectionByValue(value);
        return new OrderByItem<>(sortAttribute, sortDirection);
    }

    /**
     * Creates a {@link FilterItem} based on the given filter arguments.
     *
     * @param filterArgs                  the list of filter arguments
     * @param multipleValuesParserHandler the function to parse multiple values
     * @return the created {@link FilterItem}
     * @throws InvalidSpecificationException if the filter arguments are invalid
     */
    public static <T> FilterItem<T> createFilterItem(List<String> filterArgs, Function<String, List<String>> multipleValuesParserHandler) {
        if (!SpecificationUtil.isFilterItemValid(filterArgs)) {
            throw new InvalidSpecificationException(
                String.format(INVALID_FILTER_ARGS_COUNT, VALID_FILTER_ARGS_COUNT));
        }

        String filterAttribute = filterArgs.get(0);
        String operatorValue = filterArgs.get(1);
        String value = filterArgs.get(2);

        FilterOperator operator = SpecificationUtil.getFilterOperatorByValue(operatorValue);

        if (operator.isSingleFilterValue()) {
            return new SingleFilterItem<>(filterAttribute, operator, value);
        } else {
            List<String> values = multipleValuesParserHandler.apply(value);
            return new MultiFilterItem<>(filterAttribute, operator, values);
        }
    }

    public static boolean isFilterItemValid(List<String> filterArgs) {
        return filterArgs.size() == VALID_FILTER_ARGS_COUNT;
    }

    public static boolean isSortItemValid(List<String> sortArgs) {
        return sortArgs.size() == VALID_SORT_ARGS_COUNT;
    }
}
