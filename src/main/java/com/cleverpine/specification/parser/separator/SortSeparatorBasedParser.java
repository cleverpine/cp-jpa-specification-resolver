package com.cleverpine.specification.parser.separator;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.parser.MultipleSortParser;
import com.cleverpine.specification.util.SpecificationUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.INVALID_SORT_ARGS_COUNT;
import static com.cleverpine.specification.util.FilterConstants.VALID_SORT_ARGS_COUNT;

/**
 * A parser for multiple sort parameters based on a separator.
 * It implements the {@link MultipleSortParser} interface.
 */
@RequiredArgsConstructor
public class SortSeparatorBasedParser implements MultipleSortParser {

    @NonNull
    private final String separator;

    /**
     * Parses the given list of sort parameters into a list of {@link OrderByItem} objects.
     *
     * @param sortParams the list of sort parameters to parse.
     * @param <T>        the type of the object being sorted.
     * @return a list of {@link OrderByItem} objects parsed from the given sort parameters.
     */
    @Override
    public <T> List<OrderByItem<T>> parseSortParams(List<String> sortParams) {
        if (Objects.isNull(sortParams) || sortParams.isEmpty()) {
            return new ArrayList<>();
        }
        return sortParams.stream()
                .filter(str -> str != null && !str.isEmpty())
                .map(this::<T>createSortItem)
                .collect(Collectors.toList());
    }

    /**
     * Creates a {@link OrderByItem} from sort arguments.
     * Validates the number of arguments in the string, and throws an exception if it is not valid.
     * Extracts the sort attribute and sort direction from the sort string.
     *
     * @param sortParam the sort param containing sort arguments.
     * @return an {@link OrderByItem} parsed from the list of sort item arguments.
     * @throws InvalidSpecificationException if the list of sort item arguments is invalid.
     */
    private <T> OrderByItem<T> createSortItem(String sortParam) {
        if (Objects.isNull(sortParam)) {
            throw new InvalidSpecificationException(
                    String.format(INVALID_SORT_ARGS_COUNT, VALID_SORT_ARGS_COUNT));
        }

        List<String> sortArgs = Arrays.stream(sortParam.split(separator))
                .collect(Collectors.toList());

        return SpecificationUtil.createSortItem(sortArgs);
    }
}
