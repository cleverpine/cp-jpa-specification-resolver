package com.cleverpine.specification.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.cleverpine.specification.util.FilterConstants.BETWEEN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.EQUAL_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.GREATER_THAN_EQUAL_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.GREATER_THAN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.IN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.LESS_THAN_EQUAL_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.LESS_THAN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.LIKE_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.NOT_EQUAL_OPERATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterOperatorTest {

    @Test
    void getByValue_whenValueNotPresent_shouldReturnEmpty() {
        Optional<FilterOperator> actual = FilterOperator.getByValue("invalid");
        assertTrue(actual.isEmpty());
    }

    @Test
    void getByValue_whenValueIsPresent_shouldReturnTheAppropriateFilterOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(EQUAL_OPERATOR);
        assertTrue(actual.isPresent());
    }

    @Test
    void getByValue_onEqual_shouldReturnValidFilterOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(EQUAL_OPERATOR);
        assertEquals(FilterOperator.EQUAL, actual.get());
    }

    @Test
    void getByValue_onNotEqual_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(NOT_EQUAL_OPERATOR);
        assertEquals(FilterOperator.NOT_EQUAL, actual.get());
    }

    @Test
    void getByValue_onGreaterThan_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(GREATER_THAN_OPERATOR);
        assertEquals(FilterOperator.GREATER_THAN, actual.get());
    }

    @Test
    void getByValue_onLessThan_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(LESS_THAN_OPERATOR);
        assertEquals(FilterOperator.LESS_THAN, actual.get());
    }

    @Test
    void getByValue_onGreaterThanOrEqual_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(GREATER_THAN_EQUAL_OPERATOR);
        assertEquals(FilterOperator.GREATER_THAN_EQUAL, actual.get());
    }

    @Test
    void getByValue_onLessThanOrEqual_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(LESS_THAN_EQUAL_OPERATOR);
        assertEquals(FilterOperator.LESS_THAN_EQUAL, actual.get());
    }

    @Test
    void getByValue_onLike_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(LIKE_OPERATOR);
        assertEquals(FilterOperator.LIKE, actual.get());
    }

    @Test
    void getByValue_onBetween_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(BETWEEN_OPERATOR);
        assertEquals(FilterOperator.BETWEEN, actual.get());
    }

    @Test
    void getByValue_onIn_shouldReturnValidOperator() {
        Optional<FilterOperator> actual = FilterOperator.getByValue(IN_OPERATOR);
        assertEquals(FilterOperator.IN, actual.get());
    }

}
