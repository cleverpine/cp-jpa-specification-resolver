package com.cleverpine.specification.parser.json;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.item.OrderByItem;
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
public class SortJsonArrayParserTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueConverter valueConverter;

    @InjectMocks
    private SortJsonArrayParser sortJsonArrayParser;

    @Test
    void parseSortParam_onNullInput_shouldReturnEmptyList() {
        List<OrderByItem<Object>> actual = sortJsonArrayParser.parseSortParam(null);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseSortParam_onEmptyInput_shouldReturnEmptyList() {
        List<OrderByItem<Object>> actual = sortJsonArrayParser.parseSortParam("");
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void parseSortParam_onInvalidJson_shouldThrow() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenThrow(JsonProcessingException.class);

        assertThrows(
            InvalidSpecificationException.class,
            () -> sortJsonArrayParser.parseSortParam("invalid json")
        );
    }

    @Test
    void parseSortParam_onValidJsonArrayParamsButInvalidSortItemParamsCount_shouldThrow() throws JsonProcessingException {
        List<List<String>> parsedResult = List.of(List.of("param"));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[[\"param\", \"desc\"]]";
        assertThrows(
            InvalidSpecificationException.class,
            () -> sortJsonArrayParser.parseSortParam(jsonArray)
        );
    }

    @Test
    void sortFilterParam_onInvalidSortDirection_shouldThrow() throws JsonProcessingException {
        List<List<String>> parsedResult = List.of(List.of("param", "13"));
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[[\"param\",\"desc\"]]";
        assertThrows(
            InvalidSpecificationException.class,
            () -> sortJsonArrayParser.parseSortParam(jsonArray)
        );
    }

    @Test
    void sortFilterParam_onValidSortParamAndSortDirection_shouldReturnListOfSortItems() throws JsonProcessingException {
        List<String> parsedResult = List.of("attribute", "desc");
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(parsedResult);

        String jsonArray = "[\"attribute\",\"desc\"]";

        List<OrderByItem<Object>> actual = sortJsonArrayParser.parseSortParam(jsonArray);
        assertNotNull(actual);
        assertEquals(1, actual.size());
    }


}
