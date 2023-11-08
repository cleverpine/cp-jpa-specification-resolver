package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.FilterItem;

import java.util.List;

/**
 * Interface {@link MultipleFilterParser} for a parser that can parse multiple filter parameters and return a list of {@link FilterItem}.
 */
public interface MultipleFilterParser {

    /**
     * Parses multiple filter parameters and returns a list of {@link FilterItem}.
     * <p>
     *
     * @param filterParams a list of filter parameters to be parsed
     * @return a list of {@link FilterItem} resulting from the parsing of the filter parameters
     */
    <T> List<FilterItem<T>> parseFilterParams(List<String> filterParams);

}
