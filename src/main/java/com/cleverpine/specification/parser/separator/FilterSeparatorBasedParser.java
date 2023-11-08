package com.cleverpine.specification.parser.separator;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.SingleFilterItem;
import com.cleverpine.specification.parser.MultipleFilterParser;
import com.cleverpine.specification.util.SpecificationUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.INVALID_FILTER_ARGS_COUNT;
import static com.cleverpine.specification.util.FilterConstants.VALID_FILTER_ARGS_COUNT;

/**
 * A parser for multiple filter parameters based on a separator.
 * It implements the {@link MultipleFilterParser} interface.
 */
@RequiredArgsConstructor
public class FilterSeparatorBasedParser implements MultipleFilterParser {

    @NonNull
    private final String separator;

    @NonNull
    private final String valuesSeparator;

    /**
     * Parses a list of filter parameters and returns a list of {@link FilterItem}.
     *
     * @param filterParams the list of filter parameters to parse.
     * @return a list of {@link FilterItem} objects parsed from the filter parameters.
     */
    @Override
    public <T> List<FilterItem<T>> parseFilterParams(List<String> filterParams) {
        if (Objects.isNull(filterParams)) {
            return new ArrayList<>();
        }
        return filterParams.stream()
                .filter(str -> str != null && !str.isEmpty())
                .map(filterParam -> Arrays.stream(filterParam.split(separator)).toList())
                .filter(this::isValueArgOfFilterNotEmpty)
                .map(this::<T>createFilterItem)
                .collect(Collectors.toList());
    }

    private boolean isValueArgOfFilterNotEmpty(List<String> filterParams) {
        return filterParams.size() == VALID_FILTER_ARGS_COUNT && !filterParams.get(2).isBlank();
    }

    /**
     * Creates a {@link FilterItem} from filter arguments.
     * Validates the number of arguments in the string, and throws an exception if it is not valid.
     * Extracts the filter attribute, operator value, and value from the filter string.
     * Creates a {@link SingleFilterItem} if the operator is a single value operator,
     * or a {@link MultiFilterItem} if the operator is a multi-value operator.
     *
     * @param filterArgs the filter param containing filter arguments.
     * @return a {@link FilterItem} parsed from the list of filter item arguments.
     * @throws InvalidSpecificationException if the list of filter item arguments is invalid.
     */
    private <T> FilterItem<T> createFilterItem(List<String> filterArgs) {
        if (Objects.isNull(filterArgs)) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_FILTER_ARGS_COUNT, VALID_FILTER_ARGS_COUNT));
        }

        Function<String, List<String>> multipleValuesParserHandler = values -> Arrays.stream(values.split(valuesSeparator))
                .collect(Collectors.toList());

        return SpecificationUtil.createFilterItem(filterArgs, multipleValuesParserHandler);
    }
}
