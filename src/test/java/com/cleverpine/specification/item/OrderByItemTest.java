package com.cleverpine.specification.item;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.core.OrderBySpecification;
import com.cleverpine.specification.integration.entity.Movie;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SortDirection;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderByItemTest {

    private static final String ATTRIBUTE = "attribute";

    private static final SortDirection SORT_DIRECTION = SortDirection.ASC;

    @Test
    void constructor_onNullAttribute_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new OrderByItem<>(null, SORT_DIRECTION)
        );
    }

    @Test
    void constructor_onNullSortDirection_shouldThrow() {
        assertThrows(
                NullPointerException.class,
                () -> new OrderByItem<>(ATTRIBUTE, null)
        );
    }

    @Test
    void constructor_onValidArgs_shouldCreateOrderByItem() {
        OrderByItem<Object> actual = assertDoesNotThrow(() ->
                new OrderByItem<>(ATTRIBUTE, SORT_DIRECTION));
        assertNotNull(actual);
    }

    @Test
    void createSpecification_onNullQueryContext_throwException() {
        OrderByItem<Movie> orderByItem = new OrderByItem<>(ATTRIBUTE, SORT_DIRECTION);
        assertThrows(
                NullPointerException.class,
                () -> orderByItem.createSpecification(null));
    }

    @Test
    void createSpecification_shouldCreateOrderBySpecificationInstance() {
        OrderByItem<Movie> orderByItem = new OrderByItem<>(ATTRIBUTE, SORT_DIRECTION);

        SpecificationQueryConfig<Movie> queryConfig = SpecificationQueryConfig.<Movie>builder().build();
        QueryContext<Movie> queryContext = new QueryContext<>(queryConfig);

        CpSpecification<Movie> orderBySpecification = orderByItem.createSpecification(queryContext);

        assertEquals(OrderBySpecification.class, orderBySpecification.getClass());
    }
}
