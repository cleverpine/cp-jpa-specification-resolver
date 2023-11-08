package com.cleverpine.specification.parser.separator;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterSeparatorBasedParserTest {

    private static final String DEFAULT_SEPARATOR = ":";

    private static final String DEFAULT_VALUES_SEPARATOR = ";";

    private final FilterSeparatorBasedParser filterSeparatorBasedParser =
        new FilterSeparatorBasedParser(DEFAULT_SEPARATOR, DEFAULT_VALUES_SEPARATOR);

    @Test
    void constructor_onNullSeparator_shouldThrow() {
        assertThrows(
            NullPointerException.class,
            () -> new FilterSeparatorBasedParser(null, DEFAULT_VALUES_SEPARATOR));
    }

    @Test
    void constructor_onNullValuesSeparator_shouldThrow() {
        assertThrows(
            NullPointerException.class,
            () -> new FilterSeparatorBasedParser(DEFAULT_SEPARATOR, null));
    }

    @Test
    void constructor_onValidArgs_shouldInitializeNewInstance() {
        FilterSeparatorBasedParser actual = new FilterSeparatorBasedParser(DEFAULT_SEPARATOR, DEFAULT_VALUES_SEPARATOR);
        assertNotNull(actual);
    }

    @Test
    void parseFilterParams_onNullFilterParams_shouldReturnEmptyList() {
        List<FilterItem<Object>> actual = filterSeparatorBasedParser.parseFilterParams(null);
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseFilterParams_onEmptyFilterParams_shouldReturnEmptyList() {
        List<FilterItem<Object>> actual = filterSeparatorBasedParser.parseFilterParams(new ArrayList<>());
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseFilterParams_withNullFilterParamElement_shouldReturnEmptyCollection() {
        List<String> args = new ArrayList<>();
        args.add(null);
        List<FilterItem<Object>> actual = filterSeparatorBasedParser.parseFilterParams(args);

        assertTrue(actual.isEmpty());
    }

    @Test
    void parseFilterParams_whenFilterOperatorIsNotFound_shouldThrowException() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> filterSeparatorBasedParser.parseFilterParams(List.of("attribute:invalid:34"))
        );
    }

    @Test
    void parseFilterParams_onValidFilterParam_shouldCreateFilterItems() {
        List<String> filterParams = List.of("attribute:eq:34");
        List<FilterItem<Object>> actual = filterSeparatorBasedParser.parseFilterParams(filterParams);

        assertNotNull(actual);
        FilterItem<Object> filterItem = actual.get(0);
        assertEquals("attribute", filterItem.getAttribute());
    }

    @Test
    void parseFilterParams_onInvalidFilterParam_shouldNotCreateFilterItems() {
        List<String> filterParams = List.of("attribute:eq:");
        List<FilterItem<Object>> actual = filterSeparatorBasedParser.parseFilterParams(filterParams);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    void parseFilterParams_onDifferentFilterSeparator_shouldNotCreateFilterItems() {
        List<String> filterParams = List.of("attribute;eq;");
        List<FilterItem<Object>> actual = filterSeparatorBasedParser.parseFilterParams(filterParams);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }
}
