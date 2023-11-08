package com.cleverpine.specification.parser.json;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.SingleFilterItem;
import com.cleverpine.specification.util.ValueConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FilterJsonArrayParserTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueConverter valueConverter;

    @InjectMocks
    private FilterJsonArrayParser filterJsonArrayParser;

    @Test
    void parseFilterParam_onNullInput_shouldReturnEmptyList() {
        List<FilterItem<Object>> actual = filterJsonArrayParser.parseFilterParam(null);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseFilterParam_onEmptyInput_shouldReturnEmptyList() {
        List<FilterItem<Object>> actual = filterJsonArrayParser.parseFilterParam("");
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseFilterParam_onInvalidJson_shouldThrow() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenThrow(JsonProcessingException.class);
        assertThrows(
            InvalidSpecificationException.class,
            () -> filterJsonArrayParser.parseFilterParam("invalid json")
        );
    }

    @Test
    void parseFilterParam_onValidJsonButNotJsonArray_shouldThrow() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenThrow(JsonProcessingException.class);
        String json = "{\"param\": 14}";
        assertThrows(
            InvalidSpecificationException.class,
            () -> filterJsonArrayParser.parseFilterParam(json)
        );
    }

    @Test
    void parseFilterParam_onValidJsonArrayParamsButInvalidFilterItemParamsCount_shouldThrow() throws JsonProcessingException {
        List<List<String>> parsedResult = List.of(List.of("param"));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[[\"param\"]";
        assertThrows(
            InvalidSpecificationException.class,
            () -> filterJsonArrayParser.parseFilterParam(jsonArray)
        );
    }

    @Test
    void parseFilterParam_onInvalidFilterOperator_shouldThrow() throws JsonProcessingException {
        List<List<String>> parsedResult = List.of(List.of("param", "invalid", "13"));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[[\"param\",\"invalid\", \"12\"]]";
        assertThrows(
            InvalidSpecificationException.class,
            () -> filterJsonArrayParser.parseFilterParam(jsonArray)
        );
    }

    @Test
    void parseFilterParam_onValidFilterParamAndFilterOperator_shouldReturnListOfFilterItems() throws JsonProcessingException {
        List<List<String>> parsedResult = List.of(List.of("attribute", "eq", "15"));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[[\"attribute\",\"eq\", \"15\"]]";

        List<FilterItem<Object>> actual = filterJsonArrayParser.parseFilterParam(jsonArray);
        assertNotNull(actual);
        assertEquals(1, actual.size());
    }

    @Test
    void parseFilterParam_onValidFilterParamAndSingleFilterOperator_shouldReturnListOfSingleFilterItem() throws JsonProcessingException {
        List<List<String>> parsedResult = List.of(List.of("attribute", "eq", "15"));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[[\"attribute\",\"eq\", \"15\"]]";

        List<FilterItem<Object>> actual = filterJsonArrayParser.parseFilterParam(jsonArray);
        FilterItem<Object> filterItem = actual.get(0);

        assertEquals(SingleFilterItem.class, filterItem.getClass());
        assertEquals("attribute", filterItem.getAttribute());
    }

    @Test
    void parseFilterParam_onValidFilterParamAndMultiFilterOperator_shouldReturnListOfMultiFilterItem() throws JsonProcessingException {
        List<List<String>> parsedResult = List.of(List.of("attribute", "between", "[\\\"13\\\",\\\"18\\\"]"));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[[\"attribute\",\"between\", \"[\\\"13\\\",\\\"18\\\"]\"]]";

        List<FilterItem<Object>> actual = filterJsonArrayParser.parseFilterParam(jsonArray);
        FilterItem<Object> filterItem = actual.get(0);

        assertEquals(MultiFilterItem.class, filterItem.getClass());
        assertEquals("attribute", filterItem.getAttribute());
    }

}
