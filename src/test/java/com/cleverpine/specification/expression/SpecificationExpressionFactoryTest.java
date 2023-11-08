package com.cleverpine.specification.expression;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpecificationExpressionFactoryTest {

    @Test
    void createSpecificationExpression_whenNoExpressionIsFoundForTheGivenAttribute_shouldInitializeDefaultOne() {
        SpecificationExpression<Object, Object> specificationExpression =
            SpecificationExpressionFactory.createSpecificationExpression("attribute", new QueryContext<>(SpecificationQueryConfig.builder().build()));

        assertEquals(PathSpecificationExpression.class, specificationExpression.getClass());
    }

    @Test
    void createSpecificationExpression_whenCustomExpressionIsFoundForTheGiveAttribute_shouldCreateANewInstanceOfIt() {
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
            .customExpressionConfig()
            .addCustomSpecificationExpression("attribute", CustomSpecExpression.class)
            .end()
            .build();

        SpecificationExpression<Object, Object> specificationExpression =
            SpecificationExpressionFactory.createSpecificationExpression("attribute", new QueryContext<>(queryConfig));

        assertEquals(CustomSpecExpression.class, specificationExpression.getClass());
    }

    @Test
    void createSpecificationExpression_whenTheSpecificationExpressionHasNotTheRequiredConstructor_shouldThrow() {
        SpecificationQueryConfig<Object> queryConfig = SpecificationQueryConfig.builder()
            .customExpressionConfig()
            .addCustomSpecificationExpression("attribute", InvalidConstructorSpecExpression.class)
            .end()
            .build();

        assertThrows(
            IllegalSpecificationException.class,
            () -> SpecificationExpressionFactory.createSpecificationExpression("attribute", new QueryContext<>(queryConfig))
        );
    }

    private static class CustomSpecExpression extends SpecificationExpression<Object, Object> {

        public CustomSpecExpression(String attributePath, QueryContext<Object> queryContext) {
            super(attributePath, queryContext);
        }

        @Override
        public Expression<Object> produceExpression(Root<Object> root, CriteriaBuilder criteriaBuilder) {
            return null;
        }
    }

    private static class InvalidConstructorSpecExpression extends SpecificationExpression<Object, Object> {

        public InvalidConstructorSpecExpression(String attributePath) {
            super(attributePath, new QueryContext<>(SpecificationQueryConfig.builder().build()));
        }

        @Override
        public Expression<Object> produceExpression(Root<Object> root, CriteriaBuilder criteriaBuilder) {
            return null;
        }
    }
}
