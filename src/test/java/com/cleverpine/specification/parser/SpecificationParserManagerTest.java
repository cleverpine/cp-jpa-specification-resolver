package com.cleverpine.specification.parser;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.SingleFilterItem;
import com.cleverpine.specification.parser.json.FilterJsonArrayParser;
import com.cleverpine.specification.parser.separator.FilterSeparatorBasedParser;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.SpecificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpecificationParserManagerTest {

    @Test
    void constructor_onNullParsers_shouldCreateNewInstance() {
        SpecificationParserManager specificationParserManager =
            new SpecificationParserManager(null, null, null, null);
        assertNotNull(specificationParserManager);
    }

    @Test
    void produceFilterItems_onMissingParsersAndNoPredefinedFiltersInSpecRequest_shouldReturnEmptyList() {
        SpecificationParserManager specificationParserManager =
            new SpecificationParserManager(null, null, null, null);

        SpecificationRequest<Object> specificationRequest = SpecificationRequest.createEmpty();
        List<FilterItem<Object>> actual = specificationParserManager.produceFilterItems(specificationRequest);

        assertTrue(actual.isEmpty());
    }

    @Test
    void produceFilterItems_onMissingParsersAndSomePredefinedFiltersInSpecRequest_shouldReturnOnlyThePredefinedFilters() {
        SpecificationParserManager specificationParserManager =
            new SpecificationParserManager(null, null, null, null);

        SpecificationRequest<Object> specificationRequest = SpecificationRequest.builder()
            .withFilterItems(List.of(new SingleFilterItem<>("attribute", FilterOperator.EQUAL, "1234")))
            .build();

        List<FilterItem<Object>> actual = specificationParserManager.produceFilterItems(specificationRequest);

        assertEquals(1, actual.size());
    }

    @Test
    void produceFilterItems_onMissingSingleFilterParserAndPresentSingleFilterParam_shouldThrow() {
        SpecificationParserManager specificationParserManager =
            new SpecificationParserManager(null, null, null, null);

        SpecificationRequest<Object> specificationRequest = SpecificationRequest.builder()
            .withFilterParam("attribute:eq:1234")
            .build();

        assertThrows(IllegalSpecificationException.class,
                     () -> specificationParserManager.produceFilterItems(specificationRequest));
    }

    @Test
    void produceFilterItems_whenSingleFilterParserIsPresent_shouldParseTheInputFilterParam() {
        SpecificationParserManager specificationParserManager =
            new SpecificationParserManager(new FilterJsonArrayParser(new ObjectMapper()), null, null, null);

        SpecificationRequest<Object> specificationRequest = SpecificationRequest.builder()
            .withFilterParam("[[\"attribute\",\"eq\",\"1234\"]]")
            .withFilterItems(List.of(new SingleFilterItem<>("attribute", FilterOperator.EQUAL, "1234")))
            .build();

        List<FilterItem<Object>> actual = specificationParserManager.produceFilterItems(specificationRequest);

        assertEquals(2, actual.size());
    }

    @Test
    void produceFilterItems_shouldAggregateTheFilterItemsFromSingleMultipleAndPredefinedFilterParams() {
        SpecificationParserManager specificationParserManager =
            new SpecificationParserManager(new FilterJsonArrayParser(new ObjectMapper()), new FilterSeparatorBasedParser(":", ";"), null, null);

        SpecificationRequest<Object> specificationRequest = SpecificationRequest.builder()
            .withFilterParam("[[\"attribute\",\"eq\",\"1234\"]]")
            .withFilterParams(List.of("attribute:gt:32"))
            .withFilterItems(List.of(new SingleFilterItem<>("attribute", FilterOperator.EQUAL, "1234")))
            .build();

        List<FilterItem<Object>> actual = specificationParserManager.produceFilterItems(specificationRequest);

        assertEquals(3, actual.size());
    }
}
