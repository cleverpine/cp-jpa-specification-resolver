package com.cleverpine.specification.parser;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.util.SpecificationRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cleverpine.specification.util.FilterConstants.PARSER_NOT_PROVIDED;

/**
 * {@link SpecificationParserManager} is responsible for processing the specification requests {@link SpecificationRequest} and producing the filter and sort items.
 * The class contains SingleFilterParser, MultipleFilterParser, SingleSortParser, and MultipleSortParser instances in the constructor.
 * The {@link #produceFilterItems(SpecificationRequest)} and {@link #produceOrderByItems(SpecificationRequest)} methods process the specification requests and return the filter and sorting lists respectively.
 */
@RequiredArgsConstructor
@Builder(setterPrefix = "with")
public class SpecificationParserManager {

    private final SingleFilterParser singleFilterParser;

    private final MultipleFilterParser multipleFilterParser;

    private final SingleSortParser singleSortParser;

    private final MultipleSortParser multipleSortParser;

    /**
     * Processes the given specification request and returns the list of {@link FilterItem}.
     * The method first parses the single filter parameter and then the multiple filter parameters.
     * <p>
     *
     * @param specificationRequest the SpecificationRequest instance containing the filter and sort parameters
     * @return the list of {@link FilterItem} parsed from the specification request
     */
    public <T> List<FilterItem<T>> produceFilterItems(SpecificationRequest<T> specificationRequest) {
        return Stream.of(parseSingleFilterParam(specificationRequest),
                         parseMultipleFilterParams(specificationRequest),
                         specificationRequest.getFilterItems())
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    /**
     * Processes the given specification request and returns the list of {@link OrderByItem}.
     * The method first parses the single sort parameter and then the multiple sort parameters.
     * <p>
     *
     * @param specificationRequest the SpecificationRequest instance containing the sort and sort parameters
     * @return the list of {@link OrderByItem} parsed from the specification request
     */
    public <T> List<OrderByItem<T>> produceOrderByItems(SpecificationRequest<T> specificationRequest) {
        return Stream.of(parseSingleSortParam(specificationRequest),
                         parseMultipleSortParams(specificationRequest),
                         specificationRequest.getSortItems())
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private <T> List<FilterItem<T>> parseSingleFilterParam(SpecificationRequest<T> specificationRequest) {
        if (Objects.isNull(specificationRequest) || Objects.isNull(specificationRequest.getFilterParam())) {
            return new ArrayList<>();
        }
        if (Objects.isNull(singleFilterParser)) {
            throw new IllegalSpecificationException(PARSER_NOT_PROVIDED);
        }
        return singleFilterParser.parseFilterParam(specificationRequest.getFilterParam());
    }

    private <T> List<FilterItem<T>> parseMultipleFilterParams(SpecificationRequest<T> specificationRequest) {
        if (Objects.isNull(specificationRequest) || Objects.isNull(specificationRequest.getFilterParams())) {
            return new ArrayList<>();
        }
        if (Objects.isNull(multipleFilterParser)) {
            throw new IllegalSpecificationException(PARSER_NOT_PROVIDED);
        }
        return multipleFilterParser.parseFilterParams(specificationRequest.getFilterParams());
    }

    private <T> List<OrderByItem<T>> parseSingleSortParam(SpecificationRequest<T> specificationRequest) {
        if (Objects.isNull(specificationRequest) || Objects.isNull(specificationRequest.getSortParam())) {
            return new ArrayList<>();
        }
        if (Objects.isNull(singleSortParser)) {
            throw new IllegalSpecificationException(PARSER_NOT_PROVIDED);
        }
        return singleSortParser.parseSortParam(specificationRequest.getSortParam());
    }

    private <T> List<OrderByItem<T>> parseMultipleSortParams(SpecificationRequest<T> specificationRequest) {
        if (Objects.isNull(specificationRequest) || Objects.isNull(specificationRequest.getSortParams())) {
            return new ArrayList<>();
        }
        if (Objects.isNull(multipleSortParser)) {
            throw new IllegalSpecificationException(PARSER_NOT_PROVIDED);
        }
        return multipleSortParser.parseSortParams(specificationRequest.getSortParams());
    }
}
