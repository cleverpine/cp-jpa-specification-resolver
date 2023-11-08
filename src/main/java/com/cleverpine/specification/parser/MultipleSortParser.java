package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.OrderByItem;

import java.util.List;

/**
 * Interface {@link MultipleSortParser} for a parser that can parse multiple sort parameters and return a list of {@link OrderByItem}.
 */
public interface MultipleSortParser {

    /**
     * Parses multiple sort parameters and returns a list of {@link OrderByItem}.
     * <p>
     *
     * @param sortParams a list of sort parameters to be parsed
     * @return a list of {@link OrderByItem} resulting from the parsing of the filter parameters
     */
    <T> List<OrderByItem<T>> parseSortParams(List<String> sortParams);

}
