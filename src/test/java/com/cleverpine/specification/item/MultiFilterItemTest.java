package com.cleverpine.specification.item;

import com.cleverpine.specification.core.Between;
import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.core.In;
import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import com.cleverpine.specification.util.ValueConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MultiFilterItemTest {

    @Mock
    private ValueConverter valueConverter;

    private static final String ATTRIBUTE = "attribute";

    private static final FilterOperator FILTER_OPERATOR = FilterOperator.BETWEEN;

    private static final List<String> VALUES = List.of("14", "18");

    @Test
    void constructor_onNullAttribute_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new MultiFilterItem<>(null, FILTER_OPERATOR, VALUES)
        );
    }

    @Test
    void constructor_onNullOperator_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new MultiFilterItem<>(ATTRIBUTE, null, VALUES)
        );
    }

    @Test
    void constructor_onNullValue_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new MultiFilterItem<>(ATTRIBUTE, FILTER_OPERATOR, null)
        );
    }

    @Test
    void constructor_onValidArgs_shouldCreateFilterItem() {
        MultiFilterItem<Object> actual = assertDoesNotThrow(() ->
                new MultiFilterItem<>(ATTRIBUTE, FILTER_OPERATOR, VALUES));
        assertNotNull(actual);
    }

    @Test
    void createSpecification_whenFilterOperatorIsNotCompatibleWithMultiFilterItem_shouldThrow() {
        MultiFilterItem<Object> notCompatibleFilterItem =
                new MultiFilterItem<>(ATTRIBUTE, FilterOperator.EQUAL, VALUES);

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        assertThrows(
                IllegalSpecificationException.class,
                () -> notCompatibleFilterItem.createSpecification(queryContext, valueConverter)
        );
    }

    @Test
    void createSpecification_onValidFilterOperatorForMultiFilterItem_shouldCreateAppropriateSpecificationType() {
        MultiFilterItem<Object> inFilterItem =
                new MultiFilterItem<>(ATTRIBUTE, FilterOperator.IN, VALUES);

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        CpSpecification<Object> specification =
                inFilterItem.createSpecification(queryContext, valueConverter);

        assertEquals(In.class, specification.getClass());
    }

    @Test
    void createSpecification_whenPathToEntityAttributeIsNotPresent_shouldTakeTheAttributeFromTheFilterItemAsPath() {
        MultiFilterItem<Object> betweenFilterItem =
                new MultiFilterItem<>(ATTRIBUTE, FILTER_OPERATOR, VALUES);

        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        Between<Object> specification =
                (Between<Object>) betweenFilterItem
                        .createSpecification(queryContext, valueConverter);

        assertEquals(ATTRIBUTE, specification.getAttributePath());
    }

}
