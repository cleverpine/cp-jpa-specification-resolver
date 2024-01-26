package com.cleverpine.specification.producer;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.core.OrderBySpecification;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.OrderByItem;
import com.cleverpine.specification.parser.SpecificationParserManager;
import com.cleverpine.specification.util.QueryContext;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import com.cleverpine.specification.util.SpecificationRequest;
import com.cleverpine.specification.util.ValueConverter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class {@link ComplexSpecificationProducer} is responsible for producing complex specifications that involve
 * multiple filter items and order by items. It uses the {@link SpecificationParserManager} to parse the {@link SpecificationRequest}
 * and produce the filter and order by items. It then produces {@link CpSpecification} objects using
 * {@link SimpleSpecificationProducer} and combines them to create the final specification.
 *
 * @param <T> The type of the entity for which the specification is being produced.
 */
public class ComplexSpecificationProducer<T> {

    /**
     * The {@link SpecificationParserManager} used to parse the {@link SpecificationRequest} and produce
     * the filter and order by items.
     */
    private final SpecificationParserManager specificationParserManager;

    /**
     * The class representing the filter type, which holds the possible filter and sorting attributes.
     */
    private final Class<?> filterType;

    /**
     * The {@link ValueConverter} used to convert the values in the filter values to the correct data type.
     */
    private final ValueConverter valueConverter;

    /**
     * The {@link SpecificationQueryConfig} containing the configuration for the specification.
     */
    private final SpecificationQueryConfig<T> specificationQueryConfig;

    /**
     * The {@link SimpleSpecificationProducer} used to produce simple specifications.
     */
    private final SimpleSpecificationProducer simpleSpecificationProducer = new SimpleSpecificationProducer();

    public ComplexSpecificationProducer(SpecificationParserManager specificationParserManager,
                                        Class<?> filterType,
                                        ValueConverter valueConverter) {
        this(specificationParserManager, filterType, valueConverter, SpecificationQueryConfig.<T>builder().build());
    }

    /**
     * Creates a new instance of {@link ComplexSpecificationProducer} with the specified parser manager,
     * filter type, value converter, and configuration.
     *
     * @param specificationParserManager the parser manager used to produce filter and order-by items from a specification request
     * @param filterType                 the type of filter used in the query
     * @param valueConverter             the converter used to convert values between different types
     * @param specificationQueryConfig   the configuration used to create the query context
     */
    public ComplexSpecificationProducer(SpecificationParserManager specificationParserManager,
                                        Class<?> filterType,
                                        ValueConverter valueConverter,
                                        SpecificationQueryConfig<T> specificationQueryConfig) {
        this.specificationParserManager = specificationParserManager;
        this.filterType = filterType;
        this.valueConverter = valueConverter;
        this.specificationQueryConfig = specificationQueryConfig;
    }

    /**
     * Creates a {@link CpSpecification} instance from the given {@link SpecificationRequest} and based on the {@link SpecificationQueryConfig}.
     * The produced {@link CpSpecification} instance is composed of simple specifications produced by
     * {@link SimpleSpecificationProducer}.
     *
     * @param specificationRequest the specification request that holds the filter and sort parameters
     * @return the complex JPA Specification
     */
    public CpSpecification<T> createSpecification(SpecificationRequest<T> specificationRequest) {
        List<FilterItem<T>> requestFilterItems = specificationParserManager.produceFilterItems(specificationRequest);
        List<FilterItem<T>> specificationFilterItems = getAllSpecificationFilterItems(requestFilterItems);

        List<OrderByItem<T>> requestOrderByItems = specificationParserManager.produceOrderByItems(specificationRequest);
        List<OrderByItem<T>> specificationOrderByItems = getAllSpecificationOrderByItems(requestOrderByItems);

        QueryContext<T> queryContext = new QueryContext<>(specificationQueryConfig);

        List<CpSpecification<T>> specifications = new ArrayList<>();
        specifications.addAll(simpleSpecificationProducer
                                  .produceFilterSpecifications(filterType, specificationFilterItems, queryContext, valueConverter));
        specifications.addAll(simpleSpecificationProducer
                                  .produceOrderBySpecifications(filterType, specificationOrderByItems, queryContext));

        return conjugate(specifications, queryContext);
    }

    private CpSpecification<T> conjugate(List<CpSpecification<T>> specifications, QueryContext<T> queryContext) {
        return (root, query, criteriaBuilder) -> {
            queryContext.clearState();
            queryContext.setEntityDistinctRequired(specificationQueryConfig.isEntityDistinctRequired());
            if (queryContext.isEntityDistinctRequired()) {
                query.distinct(true);
            }

            processOrderBySpecifications(specifications, root, query, criteriaBuilder);
            Predicate[] predicates = specifications.stream()
                .filter(spec -> !isOrderBySpecification(spec))
                .map(spec -> spec.toPredicate(root, query, criteriaBuilder))
                .filter(Objects::nonNull)
                .toList()
                .toArray(new Predicate[]{});

            return criteriaBuilder.and(predicates);
        };
    }

    private List<FilterItem<T>> getAllSpecificationFilterItems(List<FilterItem<T>> filterItems) {
        ArrayList<FilterItem<T>> specificationFilterItems = new ArrayList<>();
        specificationFilterItems.addAll(filterItems);
        specificationFilterItems.addAll(specificationQueryConfig.getFilterConfig().getFilterItems());
        return specificationFilterItems;
    }

    private List<OrderByItem<T>> getAllSpecificationOrderByItems(List<OrderByItem<T>> orderItems) {
        List<OrderByItem<T>> specificationOrderItems = new ArrayList<>();
        specificationOrderItems.addAll(orderItems);
        specificationOrderItems.addAll(specificationQueryConfig.getOrderByConfig().getOrderByItems());
        return specificationOrderItems;
    }

    private void processOrderBySpecifications(List<CpSpecification<T>> specifications, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        specifications.stream()
            .filter(this::isOrderBySpecification)
            .forEach(spec -> spec.toPredicate(root, query, cb));
    }

    private boolean isOrderBySpecification(CpSpecification<T> specification) {
        return specification instanceof OrderBySpecification;
    }

}
