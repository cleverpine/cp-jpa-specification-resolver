package com.cleverpine.specification.util;

import com.cleverpine.specification.expression.PathSpecificationExpression;
import com.cleverpine.specification.expression.SpecificationExpression;
import com.cleverpine.specification.item.JoinItem;
import jakarta.persistence.criteria.JoinType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryContextTest {

    @Test
    void constructor_shouldNotRequireDistinctEntityByDefault() {
        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        // @formatter:on
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        assertFalse(queryContext.isEntityDistinctRequired());
    }

    @Test
    void getPathToEntityField_whenAttributeIsNotPresent_shouldReturnNull() {
        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        // @formatter:on
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        String actual = queryContext.getPathToEntityAttribute("invalid");
        assertNull(actual);
    }

    @Test
    void getPathToEntityFiled_whenAttributeIsPresent_shouldReturnThePath() {
        String attribute = "genreName";
        String expectedPath = "genre";
        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
                .attributePathConfig()
                .addAttributePathMapping(attribute, expectedPath)
                .end()
                .build();
        // @formatter:on

        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        String actual = queryContext.getPathToEntityAttribute(attribute);
        assertEquals(expectedPath, actual);
    }

    @Test
    void getJoinItemByAlias_whenAliasIsNotPresent_shouldReturnNull() {
        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        // @formatter:on
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        JoinItem actual = queryContext.getJoinItemByAlias("invalid alias");
        assertNull(actual);
    }

    @Test
    void getJoinItemByAlias_whenAliasIsPresent_shouldReturnThePath() {
        JoinItem expectedJoinItem = new JoinItem(Class.class, "genre", "g", JoinType.INNER);
        String alias = "g";

        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
                .joinConfig()
                .defineJoinClause(Class.class, "genre", "g", JoinType.INNER)
                .end()
                .build();
        // @formatter:on
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        JoinItem actual = queryContext.getJoinItemByAlias(alias);
        assertEquals(expectedJoinItem.getFromEntity(), actual.getFromEntity());
        assertEquals(expectedJoinItem.getJoinAttribute(), actual.getJoinAttribute());
        assertEquals(expectedJoinItem.getAlias(), actual.getAlias());
        assertEquals(expectedJoinItem.getType(), actual.getType());
    }

    @Test
    void clearState_shouldClearTheQueryJoins() {
        String alias = "g";

        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        queryContext.addJoin(alias, null);
        // @formatter:on

        assertTrue(queryContext.isJoinPresent(alias));
        queryContext.clearState();
        assertFalse(queryContext.isJoinPresent(alias));
    }

    @Test
    void clearState_shouldClearTheRequiredDistinct() {
        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder().build();
        // @formatter:on
        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);
        queryContext.setEntityDistinctRequired(true);

        assertTrue(queryContext.isEntityDistinctRequired());
        queryContext.clearState();
        assertFalse(queryContext.isEntityDistinctRequired());
    }

    @Test
    void getCustomSpecificationExpressionByAttribute_whenACustomExpressionIsNotFoundForTheAttribute_shouldReturnNull() {
        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
                .customExpressionConfig()
                .addCustomSpecificationExpression("attribute", PathSpecificationExpression.class)
                .end()
                .build();
        // @formatter:on

        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        Class<? extends SpecificationExpression> actual = queryContext.getCustomSpecificationExpressionByAttribute("not-found-attribute");
        assertNull(actual);
    }

    @Test
    void getCustomSpecificationExpressionByAttribute_whenACustomExpressionIsPresentForTheAttribute_shouldReturnTheExpressionType() {
        // @formatter:off
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
                .customExpressionConfig()
                .addCustomSpecificationExpression("attribute", PathSpecificationExpression.class)
                .end()
                .build();
        // @formatter:on

        QueryContext<Object> queryContext = new QueryContext<>(queryConfig);

        Class<? extends SpecificationExpression> actual = queryContext.getCustomSpecificationExpressionByAttribute("attribute");
        assertEquals(PathSpecificationExpression.class, actual);
    }

}
