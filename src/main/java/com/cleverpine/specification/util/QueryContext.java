package com.cleverpine.specification.util;

import com.cleverpine.specification.expression.SpecificationExpression;
import com.cleverpine.specification.item.JoinItem;
import jakarta.persistence.criteria.Join;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to hold context information required to build JPA criteria queries using
 * {@link SpecificationExpression}. It provides access to {@link Join} objects and attribute paths
 * configured in {@link SpecificationQueryConfig} to be used while building the criteria query.
 *
 * @param <T> the type of the root entity
 */
public class QueryContext<T> {

    private final Map<String, Join<?, ?>> joinsByAlias = new HashMap<>();

    private final SpecificationQueryConfig.JoinConfig<T> joinConfig;

    private final SpecificationQueryConfig.AttributePathConfig<T> attributePathConfig;

    private final SpecificationQueryConfig.CustomExpressionConfig<T> customExpressionConfig;

    private boolean entityDistinctRequired;

    /**
     * Creates a new QueryContext instance with the given {@link SpecificationQueryConfig}.
     *
     * @param specificationQueryConfig the {@link SpecificationQueryConfig} object to be used for building criteria
     *                                 queries.
     */
    public QueryContext(SpecificationQueryConfig<T> specificationQueryConfig) {
        this.joinConfig = specificationQueryConfig.getJoinConfig();
        this.attributePathConfig = specificationQueryConfig.getAttributePathConfig();
        this.customExpressionConfig = specificationQueryConfig.getCustomExpressionConfig();
    }

    /**
     * Adds a {@link Join} object with the given alias to the map of joins.
     *
     * @param alias the alias for the {@link Join} object.
     * @param join  the {@link Join} object to be added.
     */
    public void addJoin(String alias, Join<?, ?> join) {
        joinsByAlias.put(alias, join);
    }

    /**
     * Checks if a join with the given alias is present in the map of joins.
     *
     * @param alias the alias to be checked.
     * @return true if a join with the given alias is present, false otherwise.
     */
    public boolean isJoinPresent(String alias) {
        return joinsByAlias.containsKey(alias);
    }

    /**
     * Gets the {@link Join} object with the given alias from the map of joins.
     *
     * @param alias the alias for the {@link Join} object to be retrieved.
     * @return the {@link Join} object with the given alias.
     */
    public Join<?, ?> getJoinByAlias(String alias) {
        return joinsByAlias.get(alias);
    }

    /**
     * Gets the path to the entity attribute with the given name.
     *
     * @param attribute the name of the filter or sort attribute.
     * @return the path to the entity attribute.
     */
    public String getPathToEntityAttribute(String attribute) {
        return attributePathConfig.getPathToEntityAttribute(attribute);
    }

    /**
     * Gets the {@link JoinItem} object with the given alias from the join configuration object.
     *
     * @param alias the alias for the {@link JoinItem} object to be retrieved.
     * @return the {@link JoinItem} object with the given alias.
     */
    public JoinItem getJoinItemByAlias(String alias) {
        return joinConfig.getJoinItemByAlias(alias);
    }

    /**
     * Clears the state of the uqery context.
     */
    public void clearState() {
        joinsByAlias.clear();
        setEntityDistinctRequired(false);
    }

    public boolean isEntityDistinctRequired() {
        return entityDistinctRequired;
    }

    public void setEntityDistinctRequired(boolean entityDistinctRequired) {
        this.entityDistinctRequired = entityDistinctRequired;
    }

    /**
     * Returns the {@link SpecificationExpression} by an attribute
     *
     * @param attribute the attribute
     * @return Returns the {@link SpecificationExpression} by an attribute
     */
    @SuppressWarnings("rawtypes")
    public Class<? extends SpecificationExpression> getCustomSpecificationExpressionByAttribute(String attribute) {
        return customExpressionConfig.getCustomSpecificationExpressionByAttribute(attribute);
    }
}
