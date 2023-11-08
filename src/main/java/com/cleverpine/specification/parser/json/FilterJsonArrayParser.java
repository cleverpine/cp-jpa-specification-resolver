package com.cleverpine.specification.parser.json;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.SingleFilterItem;
import com.cleverpine.specification.parser.SingleFilterParser;
import com.cleverpine.specification.util.SpecificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.INVALID_FILTER_QUERY_PARAMETER;

/**
 * This class is responsible for parsing a JSON array of filter parameters and returning a list of {@link FilterItem}.
 * It implements the {@link SingleFilterParser} interface.
 * <p>
 * It uses the Jackson library to parse the JSON array into a {@link List} of {@link List}s of {@link String}s.
 * It then creates a {@link FilterItem} for each of the inner lists by extracting the filter attribute, operator, and value
 * from the list and validating them. If the operator is a single value operator, it creates a {@link SingleFilterItem} object,
 * otherwise it creates a {@link MultiFilterItem} object.
 * </p>
 * <p>
 * The class also provides a {@link #parseJson(String, TypeReference)} method to parse a JSON string into any desired
 * Java object using the Jackson library.
 * </p>
 * <p>
 * This class is annotated with {@link RequiredArgsConstructor} to generate a constructor for its final field {@code objectMapper}.
 */
@RequiredArgsConstructor
public class FilterJsonArrayParser implements SingleFilterParser {

    private static final String FILTER_PARAM_TYPE = "Json array";

    private final ObjectMapper objectMapper;

    /**
     * Parses a JSON string representing a list of filter items into a list of {@link FilterItem}s.
     * Uses Jackson's {@link ObjectMapper} to convert the JSON string into a list of lists of strings.
     * Each list of strings represents a single filter item with three elements: filter attribute, operator value, and value.
     * Creates a {@link SingleFilterItem} if the operator is a single value operator,
     * or a {@link MultiFilterItem} if the operator is a multi-value operator.
     *
     * @param filterParam the JSON string representing a list of filter items.
     * @return a list of {@link FilterItem}s parsed from the JSON string.
     * @throws InvalidSpecificationException if the JSON string is invalid or cannot be parsed.
     */
    @Override
    public <T> List<FilterItem<T>> parseFilterParam(String filterParam) {
        if (Objects.isNull(filterParam) || filterParam.isEmpty()) {
            return new ArrayList<>();
        }
        return parseJson(filterParam, new TypeReference<List<List<String>>>() {
        })
            .stream()
            .map(this::<T>createFilterItem)
            .collect(Collectors.toList());
    }

    /**
     * Method that creates a {@link FilterItem} from a list of strings representing a filter item.
     * Uses a Function to parse multiple values if the operator is a multi-value operator.
     *
     * @param filterArgs the list of strings representing a filter item.
     * @return a FilterItem created from the list of strings.
     */
    private <T> FilterItem<T> createFilterItem(List<String> filterArgs) {
        Function<String, List<String>> multipleValuesParser = values -> parseJson(values, new TypeReference<>() {
        });
        return SpecificationUtil.createFilterItem(filterArgs, multipleValuesParser);
    }

    /**
     * Method that parses a JSON string into a desired Java object using the Jackson library.
     *
     * @param json       the JSON string to be parsed.
     * @param targetType the type of the target object.
     * @return the parsed Java object.
     * @throws InvalidSpecificationException if the JSON string is invalid or cannot be parsed.
     */
    private <T> T parseJson(String json, TypeReference<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            throw new InvalidSpecificationException(
                String.format(INVALID_FILTER_QUERY_PARAMETER, FILTER_PARAM_TYPE));
        }
    }
}
