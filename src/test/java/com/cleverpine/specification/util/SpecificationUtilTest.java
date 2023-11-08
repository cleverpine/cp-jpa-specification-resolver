package com.cleverpine.specification.util;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.item.SingleFilterItem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cleverpine.specification.util.FilterConstants.EMPTY_STRING;
import static com.cleverpine.specification.util.FilterConstants.ENTITY_ATTRIBUTE_SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpecificationUtilTest {

    @Test
    void buildFullPathToEntityAttribute_onNull_shouldReturnEmptyString() {
        String actual = SpecificationUtil.buildFullPathToEntityAttribute(null);
        assertEquals(EMPTY_STRING, actual);
    }

    @Test
    void buildFullPathToEntityAttribute_onValidSingleArg_shouldReturnIt() {
        String expectedPath = "genre";
        String actual = SpecificationUtil.buildFullPathToEntityAttribute(expectedPath);
        assertEquals(expectedPath, actual);
    }

    @Test
    void buildFullPathToEntityAttribute_onValidMultipleArgs_shouldReturnTheFullPath() {
        String firstArgument = "genre";
        String secondArgument = "name";

        String actual = SpecificationUtil.buildFullPathToEntityAttribute(firstArgument, secondArgument);
        String expected = firstArgument + ENTITY_ATTRIBUTE_SEPARATOR + secondArgument;
        assertEquals(expected, actual);
    }

    @Test
    void getFilterOperatorByValue_onInvalidOperator_shouldThrow() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.getFilterOperatorByValue("invalid")
        );
    }

    @Test
    void getFilterOperatorByValue_whenTheFilterOperatorExists_shouldReturnIt() {
        FilterOperator filterOperator = SpecificationUtil.getFilterOperatorByValue("eq");
        assertEquals(FilterOperator.EQUAL, filterOperator);
    }

    @Test
    void getSortDirectionByValue_onInvalidSortDirection_shouldThrow() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.getSortDirectionByValue("invalid")
        );
    }

    @Test
    void getSortDirectionByValue_onValidSortDirection_shouldReturnIt() {
        SortDirection sortDirection = SpecificationUtil.getSortDirectionByValue("ASC");
        assertEquals(SortDirection.ASC, sortDirection);
    }

    @Test
    void createSortItem_onInvalidSortArgs_shouldThrowException() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.createSortItem(List.of("invalid"))
        );
    }

    @Test
    void createSortItem_onInvalidSortDirection_shouldThrowException() {
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.createSortItem(List.of("attribute", "invalid"))
        );
    }

    @Test
    void createSortItem_onValidSortArgs_shouldCreateNewSortItem() {
        List<String> sortArgs = List.of("attribute", "asc");
        OrderByItem<Object> actual = SpecificationUtil.createSortItem(sortArgs);

        assertEquals("attribute", actual.getAttribute());
        assertEquals(SortDirection.ASC, actual.getDirection());
    }

    @Test
    void createFilterItem_onInvalidFilterArgs_shouldThrow() {
        Function<String, List<String>> valuesParserHandler = values -> Arrays.stream(values.split(":")).collect(Collectors.toList());
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.createFilterItem(List.of("invalid"), valuesParserHandler)
        );
    }

    @Test
    void createFilterItem_onInvalidFilterOperator_shouldThrow() {
        Function<String, List<String>> valuesParserHandler = values -> Arrays.stream(values.split(":")).collect(Collectors.toList());
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.createFilterItem(List.of("attribute", "invalid", "12345"), valuesParserHandler)
        );
    }

    @Test
    void createFilterItem_onValidSingleFilterArgs_shouldCreateFilterItem() {
        Function<String, List<String>> valuesParserHandler = values -> Arrays.stream(values.split(":")).collect(Collectors.toList());
        FilterItem<Object> actual = SpecificationUtil.createFilterItem(List.of("attribute", "eq", "12345"), valuesParserHandler);

        assertEquals(SingleFilterItem.class, actual.getClass());
        assertEquals("attribute", actual.getAttribute());
    }

    @Test
    void createFilterItem_onValidMultiFilterArgs_shouldCreateFilterItem() {
        Function<String, List<String>> valuesParserHandler = values -> Arrays.stream(values.split(":")).collect(Collectors.toList());
        FilterItem<Object> actual = SpecificationUtil.createFilterItem(List.of("attribute", "in", "12345:3214"), valuesParserHandler);

        assertEquals(MultiFilterItem.class, actual.getClass());
        assertEquals("attribute", actual.getAttribute());
    }

    @Test
    void createFilterItem_onInvalidFilterParam_shouldThrowException() {
        Function<String, List<String>> valuesParserHandler = values -> Arrays.stream(values.split(":")).collect(Collectors.toList());
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.createFilterItem(List.of("filter-invalid"), valuesParserHandler)
        );
    }

    @Test
    void createFilterItem_onDifferentFilterSeparator_shouldThrowException() {
        Function<String, List<String>> valuesParserHandler = values -> Arrays.stream(values.split(":")).collect(Collectors.toList());
        assertThrows(
            InvalidSpecificationException.class,
            () -> SpecificationUtil.createFilterItem(List.of("attribute;eq;34"), valuesParserHandler)
        );
    }
}
