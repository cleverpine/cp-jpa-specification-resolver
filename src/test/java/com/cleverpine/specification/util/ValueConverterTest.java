package com.cleverpine.specification.util;

import com.cleverpine.specification.exception.InvalidSpecificationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValueConverterTest {

    @Test
    void convert_onNullValue_shouldThrowException() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convert(String.class, (String) null)
        );
    }

    @Test
    void convert_whenNoConvertFunctionForTheGivenClass_shouldReturnTheInputString() {
        ValueConverter valueConverter = new ValueConverter();

        String expectedData = "expected";
        Object actual = valueConverter.convert(Class.class, expectedData);

        assertEquals(expectedData, actual);
    }

    @Test
    void convert_onInvalidIntegerValue_shouldThrowException() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convert(Integer.class, "invalid")
        );
    }

    @Test
    void convert_onInteger_shouldReturnValidResult() {
        ValueConverter valueConverter = new ValueConverter();

        Object actual = valueConverter.convert(Integer.class, "7");
        assertEquals(7, actual);
    }

    @Test
    void convert_onInvalidLongValue_shouldThrowException() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convert(Long.class, "3211.213")
        );
    }

    @Test
    void convert_onLong_shouldReturnValidResult() {
        ValueConverter valueConverter = new ValueConverter();

        Object actual = valueConverter.convert(Long.class, "72");
        assertEquals(72L, actual);
    }

    @Test
    void convert_onInvalidDoubleValue_shouldThrowException() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convert(Double.class, "invalid")
        );
    }

    @Test
    void convert_onDouble_shouldReturnValidResult() {
        ValueConverter valueConverter = new ValueConverter();

        Object actual = valueConverter.convert(Double.class, "72.2");
        assertEquals(72.2, actual);
    }

    @Test
    void convert_onInvalidFloatValue_shouldThrowException() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convert(Float.class, "invalid")
        );
    }

    @Test
    void convert_onFloat_shouldReturnValidResult() {
        ValueConverter valueConverter = new ValueConverter();

        Object actual = valueConverter.convert(Float.class, "72.2");
        assertEquals(72.2f, actual);
    }

    @Test
    void convert_onInvalidBigDecimalValue_shouldThrowException() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convert(BigDecimal.class, "invalid")
        );
    }

    @Test
    void convert_onBigDecimal_shouldReturnValidResult() {
        ValueConverter valueConverter = new ValueConverter();

        Object actual = valueConverter.convert(BigDecimal.class, "72.2");
        assertEquals(BigDecimal.valueOf(72.2), actual);
    }

    @Test
    void convert_onInvalidUUIDValue_shouldThrowException() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convert(UUID.class, "213-4331-12414")
        );
    }

    @Test
    void convert_onUUID_shouldReturnValidResult() {
        ValueConverter valueConverter = new ValueConverter();

        Object actual = valueConverter.convert(UUID.class, "123e4567-e89b-12d3-a456-426614174000");
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), actual);
    }

    @Test
    void convertToComparable_onNonComparableValue() {
        ValueConverter valueConverter = new ValueConverter();

        assertThrows(
            InvalidSpecificationException.class,
            () -> valueConverter.convertToComparable(Class.class, "214")
        );
    }

    @Test
    void convertToComparable_onValidComparableType_shouldReturnValidResult() {
        ValueConverter valueConverter = new ValueConverter();

        Comparable<Object> actual = valueConverter.convertToComparable(Long.class, "214");

        assertNotNull(actual);
        assertEquals(214L, actual);
    }

    @Test
    void addCustomValueConverters_whenSuchConverterMappingsDoesNotExist_shouldBeAdded() {
        ValueConverter valueConverter = new ValueConverter();

        HashMap<Class<?>, Function<String, Object>> customValueConverters = new HashMap<>();
        customValueConverters.put(Date.class, value -> {
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(value);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        valueConverter.addCustomValueConverters(customValueConverters);

        Object actual = valueConverter.convert(Date.class, "31/12/1998");
        assertNotNull(actual);
    }

}
