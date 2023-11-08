package com.cleverpine.specification.parser;

import com.cleverpine.specification.item.OrderByItem;

import java.util.List;

/**
 * Interface {@link SingleSortParser} for a parser that can parse single sort parameters and return a list of {@link OrderByItem}.
 */
public interface SingleSortParser {

    /**
     * Parses multiple sort parameters and returns a list of {@link OrderByItem}.
     * <p>
     *
     * @param sortParam a string containing all sort parameters to be parsed
     * @return a list of {@link OrderByItem} resulting from the parsing of the filter parameters
     */
    <T> List<OrderByItem<T>> parseSortParam(String sortParam);
}
