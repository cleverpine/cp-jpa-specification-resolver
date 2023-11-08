package com.cleverpine.specification.parser.json;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.parser.SingleSortParser;
import com.cleverpine.specification.util.SpecificationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.INVALID_SORT_QUERY_PARAMETER;

/**
 * The {@link SortJsonArrayParser} class provides a way to parse a sort parameter represented as a JSON array.
 * Implements the {@link SingleSortParser} interface which allows it to be used as a single sort parser.
 * <p>
 * A valid sort parameter should contain an array with two values:
 * the sort attribute and the sort direction (asc/desc).
 * <p>
 * The class uses Jackson's {@link ObjectMapper} to deserialize the JSON parameter into a list of strings.
 * <p>
 * The class throws an {@link InvalidSpecificationException} if the provided sort parameter is invalid.
 */
@RequiredArgsConstructor
public class SortJsonArrayParser implements SingleSortParser {

    private static final String SORT_PARAM_TYPE = "Json array";

    private final ObjectMapper objectMapper;

    /**
     * Parses a sort parameter represented as a JSON array.
     * The provided parameter should contain an array with two values:
     * the sort attribute and the sort direction (asc/desc).
     *
     * @param sortParam the sort parameter represented as a JSON array
     * @param <T>       the type of the entity to be sorted
     * @return a list with a single {@code OrderByItem} that contains the parsed sort parameter
     * @throws InvalidSpecificationException if the provided sort parameter is invalid
     */
    @Override
    public <T> List<OrderByItem<T>> parseSortParam(String sortParam) {
        if (Objects.isNull(sortParam) || sortParam.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderByItem<T>> createdSortItem = new ArrayList<>();
        createdSortItem.add(SpecificationUtil.createSortItem(parseJson(sortParam, new TypeReference<>() {
        })));

        return createdSortItem;
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
                String.format(INVALID_SORT_QUERY_PARAMETER, SORT_PARAM_TYPE));
        }
    }
}
