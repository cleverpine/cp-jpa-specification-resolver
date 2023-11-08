package com.cleverpine.specification.util;

import com.cleverpine.specification.expression.SpecificationExpression;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.JoinItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.item.SingleFilterItem;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represents a configuration object for creating a JPA specification query.
 * The {@link SpecificationQueryConfig} object contains various nested objects that define how to build
 * the JPA specification query.
 *
 * @param <T> The entity type to which the JPA specification query is applied.
 */
@RequiredArgsConstructor
@Getter
public class SpecificationQueryConfig<T> {

    private final JoinConfig<T> joinConfig;

    private final AttributePathConfig<T> attributePathConfig;

    private final FilterConfig<T> filterConfig;

    private final OrderByConfig<T> orderByConfig;

    private final CustomExpressionConfig<T> customExpressionConfig;

    private final boolean entityDistinctRequired;

    public static <T> SpecificationQueryConfigBuilder<T> builder() {
        return new SpecificationQueryConfigBuilder<>();
    }

    public static class SpecificationQueryConfigBuilder<T> {

        private final JoinConfig<T> joinConfig = new JoinConfig<>(this);

        private final AttributePathConfig<T> attributePathConfig = new AttributePathConfig<>(this);

        private final FilterConfig<T> filterConfig = new FilterConfig<>(this);

        private final OrderByConfig<T> orderByConfig = new OrderByConfig<>(this);

        private final CustomExpressionConfig<T> customExpressionConfig = new CustomExpressionConfig<>(this);

        private boolean entityDistinctRequired;

        public JoinConfig<T> joinConfig() {
            return joinConfig;
        }

        public AttributePathConfig<T> attributePathConfig() {
            return attributePathConfig;
        }

        public FilterConfig<T> filterConfig() {
            return filterConfig;
        }

        public OrderByConfig<T> orderByConfig() {
            return orderByConfig;
        }

        public CustomExpressionConfig<T> customExpressionConfig() {
            return customExpressionConfig;
        }

        public SpecificationQueryConfigBuilder<T> entityDistinctRequired(boolean entityDistinctRequired) {
            this.entityDistinctRequired = entityDistinctRequired;
            return this;
        }

        public SpecificationQueryConfig<T> build() {
            return new SpecificationQueryConfig<>(joinConfig, attributePathConfig, filterConfig, orderByConfig, customExpressionConfig, entityDistinctRequired);
        }
    }

    public static class JoinConfig<T> {

        private final SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder;

        private final Map<String, JoinItem> joinClauses = new HashMap<>();

        private JoinConfig(SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder) {
            this.specificationQueryConfigBuilder = specificationQueryConfigBuilder;
        }

        public JoinItem getJoinItemByAlias(String alias) {
            return joinClauses.get(alias);
        }

        public JoinConfig<T> defineJoinClause(Class<?> fromEntity, String joinAttribute, String alias, JoinType joinType) {
            JoinItem joinItem = new JoinItem(fromEntity, joinAttribute, alias, joinType);
            joinClauses.put(alias, joinItem);
            return this;
        }

        public SpecificationQueryConfigBuilder<T> end() {
            return specificationQueryConfigBuilder;
        }
    }

    public static class AttributePathConfig<T> {

        private final SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder;

        private final Map<String, String> pathToEntityAttributeMappings = new HashMap<>();

        private AttributePathConfig(SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder) {
            this.specificationQueryConfigBuilder = specificationQueryConfigBuilder;
        }

        public AttributePathConfig<T> addAttributePathMapping(String filterAttribute, String pathToEntityAttribute) {
            pathToEntityAttributeMappings.put(filterAttribute, pathToEntityAttribute);
            return this;
        }

        public String getPathToEntityAttribute(String filterAttribute) {
            return pathToEntityAttributeMappings.get(filterAttribute);
        }

        public SpecificationQueryConfigBuilder<T> end() {
            return specificationQueryConfigBuilder;
        }
    }

    public static class FilterConfig<T> {

        private final SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder;

        private final List<FilterItem<T>> filterItems = new ArrayList<>();

        private FilterConfig(SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder) {
            this.specificationQueryConfigBuilder = specificationQueryConfigBuilder;
        }

        public List<FilterItem<T>> getFilterItems() {
            return new ArrayList<>(filterItems);
        }

        public FilterConfig<T> addFilter(String attribute, FilterOperator operator, Object value) {
            FilterItem<T> filterItem = new SingleFilterItem<>(attribute, operator, value.toString());
            filterItems.add(filterItem);
            return this;
        }

        public FilterConfig<T> addFilter(String attribute, FilterOperator operator, List<Object> values) {
            List<String> stringValues = values.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

            FilterItem<T> filterItem = new MultiFilterItem<>(attribute, operator, stringValues);
            filterItems.add(filterItem);
            return this;
        }

        public SpecificationQueryConfigBuilder<T> end() {
            return specificationQueryConfigBuilder;
        }
    }

    public static class OrderByConfig<T> {

        private final SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder;

        private final List<OrderByItem<T>> orderItems = new ArrayList<>();

        private OrderByConfig(SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder) {
            this.specificationQueryConfigBuilder = specificationQueryConfigBuilder;
        }

        public List<OrderByItem<T>> getOrderByItems() {
            return new ArrayList<>(orderItems);
        }

        public OrderByConfig<T> addOrderBy(String attribute, SortDirection direction) {
            OrderByItem<T> orderItem = new OrderByItem<>(attribute, direction);
            orderItems.add(orderItem);
            return this;
        }

        public SpecificationQueryConfigBuilder<T> end() {
            return specificationQueryConfigBuilder;
        }
    }

    @SuppressWarnings("rawtypes")
    public static class CustomExpressionConfig<T> {

        private final SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder;

        private final Map<String, Class<? extends SpecificationExpression>> customSpecExpressionsByAttribute = new HashMap<>();

        private CustomExpressionConfig(SpecificationQueryConfigBuilder<T> specificationQueryConfigBuilder) {
            this.specificationQueryConfigBuilder = specificationQueryConfigBuilder;
        }

        public Class<? extends SpecificationExpression> getCustomSpecificationExpressionByAttribute(String attribute) {
            return customSpecExpressionsByAttribute.get(attribute);
        }

        public CustomExpressionConfig<T> addCustomSpecificationExpression(String attribute, Class<? extends SpecificationExpression> specificationExpressionType) {
            customSpecExpressionsByAttribute.put(attribute, specificationExpressionType);
            return this;
        }

        public SpecificationQueryConfigBuilder<T> end() {
            return specificationQueryConfigBuilder;
        }
    }
}
