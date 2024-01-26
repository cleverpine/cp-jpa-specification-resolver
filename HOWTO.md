# CleverPine JPA Specification Library Integration Guide

This guide provides instructions on how to integrate and use the CleverPine JPA Specification Library
in Spring Boot and Quarkus applications. CleverPine JPA Specification Library facilitates the creation
of dynamic, database-agnostic query specifications for filtering and sorting data,
improving the efficiency and flexibility of data access layers.

## Contents
* [Integration in Spring Boot](#integration-in-spring-boot)
* [Integration in Quarkus](#integration-in-quarkus)

## Integration in Spring Boot

To use the library in Spring, you need to wrap the `CpSpecification` class
in a class that implements Spring's `Specification<T>` interface.
This allows integration with Spring Data JPA's repository abstraction.

```java
import com.cleverpine.specification.core.CpSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public class CpSpecificationWrapper<T> implements Specification<T> {
    private final CpSpecification<T> cpSpecification;

    public CpSpecificationWrapper(CpSpecification<T> cpSpecification) {
        this.cpSpecification = cpSpecification;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        return this.cpSpecification.toPredicate(root, query, criteriaBuilder);
    }
}
```

### Using the Wrapper in Your Code

Create and use the wrapper in your repositories or services:

```java
private CpSpecificationWrapper<SomeEntity> createSpecification(List<String> filter, List<String> sort) {
    var specificationRequest = SpecificationRequest.<SomeEntity>builder()
        .withFilterParams(filter)
        .withSortParams(sort)
        .build();
    var specification = eventSpecificationProducer.createSpecification(specificationRequest);
    return new CpSpecificationWrapper<>(specification);
}
```

## Integration in Quarkus

### Create an Abstract class for your Repositories

In Quarkus, create an abstract repository class that uses `PanacheRepository` and handles `CpSpecification`:

```java
import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.producer.ComplexSpecificationProducer;
import com.cleverpine.specification.util.SpecificationRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public abstract class AbstractSpecificationRepository<E, P extends ComplexSpecificationProducer<E>> implements PanacheRepository<E> {

    /**
     * The specification producer.
     */
    @Inject
    protected P specificationProducer;

    /**
     * Use to get a specification result.
     *
     * @param specificationRequest the specification request
     * @param entityType           the entity type
     * @return the specification result
     */
    protected List<E> getSpecificationResult(SpecificationRequest<E> specificationRequest, Class<E> entityType) {
        return createTypedQuery(specificationRequest, entityType).getResultList();
    }

    /**
     * Use to get a paginated specification result.
     *
     * @param specificationRequest the specification request
     * @param entityType           the entity type
     * @param pageIndex            the page index, which is 1-based
     * @param pageSize             the page size
     * @return the paginated specification result
     */
    protected PageDataDto<E> getPaginatedSpecificationResult(SpecificationRequest<E> specificationRequest, Class<E> entityType, int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            throw new IllegalArgumentException("Page index must be >= 1 : " + pageIndex);
        } else if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be > 0 : " + pageSize);
        }

        long totalRecords = getTotalRecordsCount(specificationRequest, entityType);
        int totalPages = calculateNumberOfPages(totalRecords, pageSize);

        TypedQuery<E> query = createTypedQuery(specificationRequest, entityType);
        query.setFirstResult((pageIndex - 1) * pageSize);
        query.setMaxResults(pageSize);
        final List<E> data = query.getResultList();

        return PageDataDto.<E>builder()
            .currentPage(pageIndex)
            .totalElements(totalRecords)
            .totalPages(totalPages)
            .data(data)
            .build();
    }

    /**
     * Use to get the total number of records for a given specification request.
     *
     * @param specificationRequest the specification request
     * @param entityType           the entity type
     * @return the total number of records for a given specification request
     */
    protected Long getTotalRecordsCount(SpecificationRequest<E> specificationRequest, Class<E> entityType) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<E> root = countQuery.from(entityType);
        countQuery.select(criteriaBuilder.count(root));
        CpSpecification<E> specification = specificationProducer.createSpecification(toRequestWithoutSort(specificationRequest));
        Predicate predicate = specification.toPredicate(root, countQuery, criteriaBuilder);
        countQuery.where(predicate);
        return getEntityManager().createQuery(countQuery).getSingleResult();
    }

    private CriteriaQuery<E> createSpecificationQuery(CpSpecification<E> specification, Class<E> entityType) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = criteriaBuilder.createQuery(entityType);
        Root<E> root = query.from(entityType);
        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
        query.where(predicate);
        return query;
    }

    private TypedQuery<E> createTypedQuery(SpecificationRequest<E> specificationRequest, Class<E> entityType) {
        CriteriaQuery<E> criteriaQuery = createSpecificationQuery(specificationProducer.createSpecification(specificationRequest), entityType);
        return getEntityManager().createQuery(criteriaQuery);
    }

    private SpecificationRequest<E> toRequestWithoutSort(SpecificationRequest<E> specificationRequest) {
        return new SpecificationRequest<>(
            specificationRequest.getFilterParam(),
            specificationRequest.getFilterParams(),
            specificationRequest.getFilterItems(),
            null, null, null
        );
    }

    private int calculateNumberOfPages(long totalRecords, int pageSize) {
        return (int) Math.ceil((double) totalRecords / pageSize);
    }
}
```

And the `PageDataDto` class:

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDataDto<T> {
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private List<T> data;

    public int getSize() {
        return data.size();
    }
}
```

### Using the Abstract Repository in Your Code

Implement the abstract class in your repository:

```java
@ApplicationScoped
public class SomeRepository extends AbstractSpecificationRepository<SomeEntity, SomeSpecificationProducer> {

    public PageDataDto<SomeRepository> searchByFamilyAndFilters(List<String> filter, List<String> sort, int page, int size) {
        return getPaginatedSpecificationResult(
            createSpecificationRequest(filter, sort),
            SomeRepository.class,
            page,
            size
        );
    }

    private SpecificationRequest<SomeEntity> createSpecificationRequest(List<String> filter, List<String> sort) {
        return SpecificationRequest.<SomeEntity>builder()
            .withFilterParams(filter)
            .withSortParams(sort)
            .build();
    }
}
```

## Additional Notes

* *Flexibility*: The library provides flexibility in defining dynamic queries,
catering to complex business logic and filtering requirements.
* *Compatibility*: The library is tested and compatible with Spring Data JPA and Quarkus Panache.

This guide aims to streamline the integration process of the library,
offering a robust and flexible approach to handling dynamic queries in Java applications.
For any additional information about the queries or support, please refer to the library's
[official documentation](README.md).
