package com.cleverpine.specification.parser.separator;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.OrderByItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortSeparatorBasedParserTest {

    private static final String DEFAULT_SEPARATOR = ":";

    private final SortSeparatorBasedParser sortSeparatorBasedParser = new SortSeparatorBasedParser(DEFAULT_SEPARATOR);

    @Test
    void constructor_onNullSeparator_shouldThrow() {
        assertThrows(
            NullPointerException.class,
            () -> new SortSeparatorBasedParser(null));
    }

    @Test
    void constructor_onValidArgs_shouldInitializeNewInstance() {
        SortSeparatorBasedParser actual = new SortSeparatorBasedParser(DEFAULT_SEPARATOR);
        assertNotNull(actual);
    }

    @Test
    void parseSortParams_onNullSortParams_shouldReturnEmptyList() {
        List<OrderByItem<Object>> actual = sortSeparatorBasedParser.parseSortParams(null);
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseSortParams_onEmptySortParams_shouldReturnEmptyList() {
        List<OrderByItem<Object>> actual = sortSeparatorBasedParser.parseSortParams(new ArrayList<>());
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseSortParams_withNullSortParamElement_shouldThrowException() {
        List<String> args = new ArrayList<>();
        args.add(null);
        List<OrderByItem<Object>> actual = sortSeparatorBasedParser.parseSortParams(args);

        assertTrue(actual.isEmpty());
    }

    @Test
    void parseSortParams_onInvalidSortParam_shouldThrowException() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> sortSeparatorBasedParser.parseSortParams(List.of("sort-invalid"))
        );
    }

    @Test
    void parseSortParams_onDifferentSortSeparator_shouldThrowException() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> sortSeparatorBasedParser.parseSortParams(List.of("attribute;asc"))
        );
    }

    @Test
    void parseSortParams_whenSortDirectionIsNotFound_shouldThrowException() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> sortSeparatorBasedParser.parseSortParams(List.of("attribute:invalid"))
        );
    }

    @Test
    void parseSortParams_onValidSortParam_shouldCreateSortItems() {
        List<String> sortParams = List.of("attribute:desc");
        List<OrderByItem<Object>> actual = sortSeparatorBasedParser.parseSortParams(sortParams);

        assertNotNull(actual);
        OrderByItem<Object> filterItem = actual.get(0);
        assertEquals("attribute", filterItem.getAttribute());
    }
}
