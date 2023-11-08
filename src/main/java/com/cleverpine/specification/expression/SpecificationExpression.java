package com.cleverpine.specification.expression;

import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.item.JoinItem;
import com.cleverpine.specification.util.QueryContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

import static com.cleverpine.specification.util.FilterConstants.ENTITY_ATTRIBUTE_SEPARATOR;
import static com.cleverpine.specification.util.FilterConstants.INVALID_JOIN;
import static com.cleverpine.specification.util.FilterConstants.JOIN_NOT_DEFINED;

/**
 * The SpecificationExpression class is an abstract class that serves as the base class for all specification
 * expressions. It provides a common interface for producing JPA expressions, and contains methods for building path
 * expressions to entity attributes.
 *
 * @param <T> the type of entity being queried
 * @param <G> the type of attribute being queried
 */
@RequiredArgsConstructor
@Getter(value = AccessLevel.PROTECTED)
public abstract class SpecificationExpression<T, G> {

    protected final String attributePath;

    protected final QueryContext<T> queryContext;

    /**
     * Produce the JPA expression from the provided root and criteria builder.
     *
     * @param root            the root object of the entity
     * @param criteriaBuilder the builder for creating Criteria Query objects
     * @return the JPA expression
     */
    public abstract Expression<G> produceExpression(Root<T> root, CriteriaBuilder criteriaBuilder);

    /**
     * Builds a JPA path expression to the entity attribute by building a join or fetch path/s from the root.
     *
     * @param <S>                 the type of attribute of the path expression, which will be returned
     * @param entityAttributePath the full path to the entity attribute
     * @param root                the root object of the query
     * @return the path expression to the entity attribute
     */
    protected <S> Path<S> buildPathExpressionToEntityAttribute(String entityAttributePath, Root<T> root) {
        return queryContext.isEntityDistinctRequired()
            ? buildFetchPathToAttribute(root, entityAttributePath)
            : buildJoinPathToAttribute(root, entityAttributePath);
    }

    /**
     * Builds a join path to the attribute in the entity, starting from the root. It splits the attribute's path.
     *
     * @param <S>  the type of attribute of the join path, which will be returned
     * @param root the root object of the query
     * @return the join path to the attribute
     */
    private <S> Path<S> buildJoinPathToAttribute(Root<T> root, String path) {
        if (isSingleAttributePath(path)) {
            return root.get(path);
        }

        String[] attributePathTokens = path.split("\\" + ENTITY_ATTRIBUTE_SEPARATOR);
        Deque<String> joinPathAliasesQueue = getJoinPathAliases(attributePathTokens);

        From<?, ?> joinPath = root;

        while (!joinPathAliasesQueue.isEmpty()) {
            joinPath = joinWith(joinPath, joinPathAliasesQueue.poll());
        }

        String attribute = attributePathTokens[attributePathTokens.length - 1];
        return joinPath.get(attribute);
    }

    /**
     * Builds a fetch path to the attribute in the entity, starting from the root. It splits the attribute's path.
     *
     * @param <S>  the type of attribute of the path expression, which will be returned
     * @param root the root object of the query
     * @return the fetch path to the attribute
     */
    private <S> Path<S> buildFetchPathToAttribute(Root<T> root, String path) {
        if (isSingleAttributePath(path)) {
            return root.get(path);
        }

        String[] attributePathTokens = path.split("\\" + ENTITY_ATTRIBUTE_SEPARATOR);
        Deque<String> joinPathAliasesQueue = getJoinPathAliases(attributePathTokens);

        From<?, ?> joinPath = root;

        while (!joinPathAliasesQueue.isEmpty()) {
            joinPath = joinFetchWith(joinPath, joinPathAliasesQueue.poll());
        }

        String attribute = attributePathTokens[attributePathTokens.length - 1];
        return joinPath.get(attribute);
    }

    /**
     * Gets the aliases of the join paths in the attribute path, in the order they appear.
     *
     * @param attributePathToken the path to the attribute, split into tokens
     * @return the aliases of the join paths
     */
    private Deque<String> getJoinPathAliases(String[] attributePathToken) {
        Deque<String> joinPathAliases = new ArrayDeque<>();
        Arrays.stream(Arrays.copyOfRange(attributePathToken, 0, attributePathToken.length - 1))
            .forEach(joinPathAliases::offer);
        return joinPathAliases;
    }

    private Join<?, ?> joinWith(From<?, ?> from, String joinPathAlias) {
        if (queryContext.isJoinPresent(joinPathAlias)) {
            return queryContext.getJoinByAlias(joinPathAlias);
        }

        JoinItem joinItem = queryContext.getJoinItemByAlias(joinPathAlias);
        validateJoin(joinItem, from.getJavaType(), joinPathAlias);

        Join<?, ?> joinPath = from.join(joinItem.getJoinAttribute(), joinItem.getType());
        queryContext.addJoin(joinPathAlias, joinPath);
        return joinPath;
    }

    private Join<?, ?> joinFetchWith(From<?, ?> from, String joinPathAlias) {
        if (queryContext.isJoinPresent(joinPathAlias)) {
            return queryContext.getJoinByAlias(joinPathAlias);
        }

        JoinItem joinItem = queryContext.getJoinItemByAlias(joinPathAlias);
        validateJoin(joinItem, from.getJavaType(), joinPathAlias);

        Join<?, ?> joinPath = (Join<?, ?>) from.fetch(joinItem.getJoinAttribute(), joinItem.getType());
        queryContext.addJoin(joinPathAlias, joinPath);
        return joinPath;
    }

    private void validateJoin(JoinItem joinItem, Class<?> joinType, String joinPathAlias) {
        if (Objects.isNull(joinItem)) {
            throw new IllegalSpecificationException(String.format(JOIN_NOT_DEFINED, joinPathAlias));
        }
        if (!joinType.equals(joinItem.getFromEntity())) {
            throw new IllegalSpecificationException(
                String.format(INVALID_JOIN, joinItem.getJoinAttribute(), joinType.getSimpleName(), joinItem.getFromEntity().getSimpleName()));
        }
    }

    private boolean isSingleAttributePath(String path) {
        return !path.contains(ENTITY_ATTRIBUTE_SEPARATOR);
    }
}
