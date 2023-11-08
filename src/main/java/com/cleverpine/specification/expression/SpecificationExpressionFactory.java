package com.cleverpine.specification.expression;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.util.QueryContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.INVALID_EXPRESSION_CREATION;

public class SpecificationExpressionFactory {

    /**
     * Creates a new SpecificationExpression from the given attribute path and query context. It creates a custom
     * {@link SpecificationExpression} if there is a configured custom specification class for the given attribute path,
     * otherwise it initializes a {@link PathSpecificationExpression} instance.
     *
     * @param attributePath the attribute path to create the expression for
     * @param queryContext  the query context to use in creating the expression
     * @param <G>           the type of the expression
     * @param <T>           the type of the entity being queried
     * @return a new SpecificationExpression for the given attribute path and query context
     * @throws IllegalSpecificationException if the {@link SpecificationExpression} cannot be initialized.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <G, T> SpecificationExpression<T, G> createSpecificationExpression(String attributePath,
                                                                                     QueryContext<T> queryContext) {
        Class<? extends SpecificationExpression> customSpecificationExpressionType =
            queryContext.getCustomSpecificationExpressionByAttribute(attributePath);

        Class<? extends SpecificationExpression> specificationExpression =
            Objects.nonNull(customSpecificationExpressionType) ?
                customSpecificationExpressionType :
                PathSpecificationExpression.class;
        try {
            return (SpecificationExpression<T, G>) specificationExpression
                .getDeclaredConstructor(String.class, QueryContext.class)
                .newInstance(attributePath, queryContext);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new IllegalSpecificationException(
                String.format(INVALID_EXPRESSION_CREATION, specificationExpression.getSimpleName()));
        }
    }
}
