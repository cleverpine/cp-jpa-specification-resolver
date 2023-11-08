package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.FilterItem;

import java.util.List;

/**
 * Interface {@link SingleFilterParser} for a parser that can parse single filter parameters and return a list of {@link FilterItem}.
 */
public interface SingleFilterParser {

    /**
     * Parses multiple filter parameters and returns a list of {@link FilterItem}.
     * <p>
     *
     * @param filterParam a string containing all filter parameters to be parsed
     * @return a list of {@link FilterItem} resulting from the parsing of the filter parameters
     */
    <T> List<FilterItem<T>> parseFilterParam(String filterParam);

}
