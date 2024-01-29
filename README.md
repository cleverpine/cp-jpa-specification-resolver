# CleverPine JPA Specification Resolver

## Description

The filtering of resources by various dynamic and complex criteria is an essential part in many RESTful APIs. Unfortunately, there is no established standard for sending the filtering arguments to a RESTful API, on another hand, the filtering database queries are unique for each scenario, which requires some configurations.

```
GET http://my-back-end/api/movies?filter=title:eq:Fast and Furious
```

The following library consists of two independent main features:

* Building a JPA Specification based on multiple complex filter or sort criteria.
* Parsing a single or multiple query parameters in order to extract all filter and sort items from it.

## Table of contents

* [Requirements](#requirements)
* [Library API](#library-api)
* [Basic usage](#basic-usage)
* [Features](#features)
* [Contribution](#contribution)

## Requirements

In order to add the library to the classpath of a Maven project, you need the following dependency.

```xml
<dependency>
    <groupId>com.cleverpine</groupId>
    <artifactId>cp-jpa-specification-resolver</artifactId>
    <version>${cp-jpa-specification-resolver.version}</version>
</dependency>
```

Do not forget to pick an appropriate version. [Link to the library in Maven Central Repository](https://mvnrepository.com/artifact/com.cleverpine/cp-jpa-specification-resolver).

## Library API

Familiarize yourself with the main public classes and interfaces in order to take advantage of the library's full potential.

* #### ValueConverter

When parsing the filter value from the query string, the given value will always be а string, but the JPA Specification works under the hood with statically typed values. This class can convert the string value to the target attribute's type. It is possible to add additional custom mappings for special use cases. See how to configure the ValueConverter [here](#customize-value-converter).

* #### SpecificationParserManager

This is a class for parsing the input query parameter and preparing it for a JPA Specification creation (producing filter and sort items). As above already mentioned there is no established standard for interpreting the filter parameter. That is why this interface can have many implementations based on your use case. For example the FilterJsonArrayParser is an implementation of the interface, which prepares a json array string for a JPA Specification creation. There are single and multiple parsers, that can be given to the SpecificationParserManger. See how to configure the SpecificationParserManager below.

```java
SpecificationParserManager specificationParserManager = SpecificationParserManager.builder()
        .withMultipleFilterParser(new FilterSeparatorBasedParser(":", ";"))
        .withMultipleSortParser(new SortSeparatorBasedParser(":"))
        .build();
```

* #### SpecificationRequest

This class is required to create a JPA Specification. You can pass different filter and sort parameters or items that are used to build the end Specification.

```java
SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
        .withFilterParams(filterParams)
        .build();
```

* #### ComplexSpecificationProducer

This is the main class that creates the target JPA Specification for a single entity.

## Basic usage

The library can be used very easily in a simple Maven project. Just initialize the classes mentioned above. See the example:

```java
public void initialize() {
    ValueConverter valueConverter = new ValueConverter();
    // FilterJsonArrayParser can be replaced by any other implementation of FilterParamParser interface
    MultipleFilterParser multipleFilterParser = new FilterSeparatorBasedParser(":", ";");
    MultipleSortParser multipleSortParser = new SortSeparatorBasedParser(":");
    SpecificationParserManager specificationParserManager = SpecificationParserManager.builder()
        .withMultipleFilterParser(multipleFilterParser)
        .withMultipleSortParser(multipleSortParser)
        .build();
    ComplexSpecificationProducer<Movie> movieSpecificationProducer =
            new ComplexSpecificationProducer<>(specificationParserManager, MovieCriteria.class, valueConverter);
    SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
        .withFilterParam("here goes the filter query parameter")
        .withFilterParams(List.of("here goes each filter param"))
        .withFilterItems(List.of(new SingleFilterItem<>("attribute", FilterOperator.EQUAL, "1234")))
        .build();
    Specification<Movie> specification = movieSpecificationProducer.createSpecification(specificationRequest);
}
```

The MovieCriteria class contains all the attributes that can be part of the filtering. This class is used for validating the filtering and sorting attributes.

A single ComplexSpecificationProducer object instance can be used for the production of JPA Specifications only for a single entity. Taking this fact into account the ComplexSpecificationProducer can be extended for each entity if it is needed. This means that a new class can be created.

The library gives the ability to configure each entity specification producer - adding mappings from the criteria attribute to the entity attribute path, defining joins, customizing the filter expressions etc.... More about the configuration of a single complex specification producer - see the section below.

The example below is a more concrete for producing Movie entity JPA specifications.

* Entities / Data model

```java
@Entity
class Movie {

    @Id
    private Long id;

    @Column
    private String title;

    @ManyToOne
    private Genre genre;

    @ManyToMany
    private Set<Actor> actors;
}

@Entity
class Genre {

    @Id
    private Long id;

    @Column
    private String name;
}

@Entity
class Actor {

    @Id
    private Long id;

    @Column
    private String fullName;
}
```

* Movie criteria

This class contains the filtering and sorting attributes. It is required to filter the movies only by the following criteria attributes.

```java
class MovieCriteria {

    private Long id;

    private String title;

    private String genreName;

    private String actorName;

    private Long movieTitle;

}
```

* Movie specification producer

This is the class that produces JPA specification for the movie entity. The JPA Specification producer can be configured with a SpecificationQueryConfig class that is based on a builder pattern. We can define join clauses, filter attribute path mapping to the entity attribute, custom expression and default filter and sort items. See the example below.

```java
import com.cleverpine.specification.parser.SingleFilterParser;
import com.cleverpine.specification.parser.SingleFilterParser;
import com.cleverpine.specification.parser.SpecificationParserManager;
import com.cleverpine.specification.producer.ComplexSpecificationProducer;
import com.cleverpine.specification.util.SortDirection;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import com.cleverpine.specification.util.SpecificationUtil;

class MovieSpecificationProducer extends ComplexSpecificationProducer<Movie> {

    private static final String MOVIE_GENRE_JOIN_ALIAS = "g";

    private static final String MOVIE_ACTORS_JOIN_ALIAS = "a";

    private static final SpecificationQueryConfig<Movie> SPECIFICATION_QUERY_CONFIG;

    static {
        SPECIFICATION_QUERY_CONFIG = SpecificationQueryConfig.builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", MOVIE_GENRE_JOIN_ALIAS, JoinType.INNER)
                    .defineJoinClause(Movie.class, "actors", MOVIE_ACTORS_JOIN_ALIAS, JoinType.INNER)
                    .end()
                .attributePathConfig()
                    // add paths to entity attributes only if a criteria attribute does not match the entity attribute
                    .addAttributePathMapping("movieTitle", "title")
                    // add the join alias if an attribute from a relationship is required
                    .addAttributePathMapping("genreName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_GENRE_JOIN_ALIAS, "name"))
                    .addAttributePathMapping("actorName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_ACTORS_JOIN_ALIAS, "fullName"))
                    .end()
                .customExpressionConfig()
                    .addCustomSpecificationExpression("filter-attr", CustomFilterExpression.class)
                    .end()
                .orderByConfig()
                    .addOrderBy("genreName", SortDirection.DESC)
                    .end()
                .build();
    }

    public MovieSpecificationProducer(SpecificationParserManager specificationParserManager, ValueConverter valueConverter) {
        super(specificationParserManager, MovieCriteria.class, valueConverter, SPECIFICATION_QUERY_CONFIG);
    }
}
```

_When the criteria attribute name is the same as the entity attribute, it is not necessary to add a path mapping for this filter attribute. That is why there is no mapping in the specification producer above for the title of the movie._

_Note! The defineJoinClause method in the joinConfig contains the join definitions. The join is created on-demand. The above specification producer will join the movies with actors only if a filter item with the actorName attribute is present._

**For more detailed usage of the library in Spring and Quarkus see the [HOWTO.md](HOWTO.md) file.**

## Features

#### Supported Filter Operators

Each filter item consists of three units - criteria attribute, filter operator and value/s. The supported filter operators with their operator values are:

* Equal / eq
* Not Equal / neq
* Greater Than / gt
* Less Than / lt
* Greater Than or Equal / gte
* Less Than or Equal / lte
* Like / like
* Between / between
* In / in

#### Supported Sort Directions and Multi-Level Sorting

Each sort item in the request consists of two parts - the sort attribute and the sort direction. The supported sort directions are:

* Ascending (`asc`)
* Descending (`desc`)

Additionally, our system supports multi-level sorting, which allows sorting by more than one attribute in a single query. This feature is particularly useful when you need to organize data based on primary and secondary (or more) sorting criteria.

##### How Multi-Level Sorting Works:

Multi-level sorting is applied in the order of the specified sort parameters. The first sort parameter takes precedence, and subsequent sort parameters are used to further refine the sorting within the groups of data already sorted by the preceding parameters.

##### Example of Multi-Level Sorting:

Consider a dataset with attributes `Name` and `Age`.
If we want to sort primarily by `Name` in ascending order and then by `Age` in descending order
within each name, we would send two sort parameters:

* First, sort by `Name` ascending: `sort=Name:asc`
* Then, sort by `Age` descending within each name group: `sort=Age:desc`

The resulting order would first group and sort all entities by `Name` in ascending order.
Then, within each group of names, the entities would be sorted by `Age` in descending order.

#### Joins on-demand

For each entity specification producer you can define several joins in order to filter the result based on some entity relationship attributes. The JoinItem class instances are joins on-demand. The producer will join the entities only if an appropriate filter attribute is present. Each JoinItem has an alias declaration. The aliases are present to make complex queries more manageable. If you want to access some nested entity properties, you should use the alias in order to do that. See this example.

#### Additional filter items

When creating the specification, you can pass additional filter requests parameters. This makes your queries much more flexible and manageable. The attribute in the filter request should be part of the criteria class.

```java
SpecificationRequest<Movie> specificationRequestWithAdditionalFilters = SpecificationRequest.<Movie>builder()
        .withFilterParams(filterParams)
        .withFilterItems(List.of(new SingleFilterItem("attribute", FilterOperator.EQUALS, "1234")))
        .build();
Specification<Movie> specification = movieSpecificationProducer.createSpecification(specificationRequestWithAdditionalFilters);
```

You can define default filter items directly in the SpecificationQueryConfig. The defined filter items will always be applied for the produced Specifications.

```java
import com.cleverpine.specification.util.FilterOperator;

class MovieSpecificationProducer extends ComplexSpecificationProducer<Movie> {

    private static final SpecificationQueryConfig<Movie> SPECIFICATION_QUERY_CONFIG;

    static {
        SPECIFICATION_QUERY_CONFIG = SpecificationQueryConfig.builder()
                .filterConfig()
                    .addFilter("attribute", FilterOperator.EQUAL, "123456")
                    .end()
                .build();
    }

    public MovieSpecificationProducer(SpecificationParserManager specificationParserManager, ValueConverter valueConverter) {
        super(specificationParserManager, MovieCriteria.class, valueConverter, SPECIFICATION_QUERY_CONFIG);
    }
}
```

This filter above will always be applied to the queries.

#### Custom Expression

There are some scenarios that require some custom expression to be executed at the database level. For example - string concatenation, arithmetic expression etc.

```java
class MovieTitleAndGenreNameConcatExpression extends SpecificationExpression<Movie, String> {

    public MovieTitleAndGenreNameConcatExpression(String attributePath,
                                                  QueryContext<T> queryContext) {
        super(attributePath, queryContext);
    }

    @Override
    public Expression<String> produceExpression(Root<Movie> root, CriteriaBuilder criteriaBuilder) {
        Path<String> titlePath = buildPathExpressionToEntityAttribute("title");
        Path<String> genrePath = buildPathExpressionToEntityAttribute("g.name");
        return criteriaBuilder.concat(titlePath, genrePath);
    }
}


class MovieSpecificationProducer extends ComplexSpecificationProducer<Movie> {

    public static final String MOVIE_GENRE_JOIN_ALIAS = "g";

    private static final SpecificationQueryConfig<Movie> SPECIFICATION_QUERY_CONFIG;

    static {
        SPECIFICATION_QUERY_CONFIG = SpecificationQueryConfig.builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", MOVIE_GENRE_JOIN_ALIAS, JoinType.INNER)
                    .end()
                .customExpressionConfig()
                    .addCustomSpecificationExpression("movieTitleGenre", MovieTitleAndGenreNameConcatExpression.class)
                    .end()
                .build();
    }

    public MovieSpecificationProducer(SpecificationParserManager specificationParserManager, ValueConverter valueConverter) {
        super(specificationParserManager, MovieCriteria.class, valueConverter, SPECIFICATION_QUERY_CONFIG);
    }
}
```

When there is a filter attribute for the _movieTitleGenre_, the custom expression will be applied for it.

_See that the path in the custom expression class is the full path to the attribute of the entity_.

#### Customize value converter

You can add additional custom value converter based on your use case. A value converter can be added for each data type (primitive and custom).

```java
Map<Class<?>, Function<String, Object>> customValueConverters = new HashMap<>();
customValueConverters.put(MovieStatus.class, (value) -> MovieStatus.valueOf(value));
ValueConverter valueConverter = new ValueConverter();
valueConverter.addCustomValueConverters(customValueConverters);
```

#### Specification producer configuration

The class _SpecificationQueryConfig_ is based on the builder pattern. You can configure the joins, paths to entity attributes, sorting attributes and adding additional filter criteria.

```java
SpecificationQueryConfig<Movie> queryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                    .defineJoinClause(Movie.class, "genre", MOVIE_GENRE_JOIN_ALIAS, JoinType.INNER)
                    .defineJoinClause(Movie.class, "actors", MOVIE_ACTORS_JOIN_ALIAS, JoinType.INNER)
                    .end()
                .attributePathConfig()
                    .addAttributePathMapping("movieTitle", "title")
                    .addAttributePathMapping("genreName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_GENRE_JOIN_ALIAS, "name"))
                    .addAttributePathMapping("actorName", SpecificationUtil.buildFullPathToEntityAttribute(MOVIE_ACTORS_JOIN_ALIAS, "fullName"))
                    .end()
                .orderByConfig()
                    .addOrderBy("genreName", SortDirection.DESC)
                    .end()
                .customExpressionConfig()
                    .addCustomSpecificationExpression("movieTitleGenre", MovieTitleAndGenreNameConcatExpression.class)
                    .end()
                .entityDistinctRequired(true)
                .build();
```

#### Sorting

You can sort by several attributes. This is configurable in the _SpecificationQueryConfig_ class or you can pass soring parameter when creating the specification.

#### Remove duplicates

You are able to do entity distinction. During the configuration of a specification producer in the _SpecificationQueryConfigBuilder_ class you can pass a value to the _entityDistinctRequired_ method. Keep in mind that, if you use the distinction in combination with sorting by an attribute, the attribute is fetched.

## Contribution

The library is not fully-featured. If you need something else, that is not present in the library. Do not hesitate to contribute to it. Create a pull request and add some tests. It is open for extension. :)
