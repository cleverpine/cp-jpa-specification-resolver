package com.cleverpine.specification.integration.test;

import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.exception.IllegalSpecificationException;
import com.cleverpine.specification.exception.InvalidSpecificationException;
import com.cleverpine.specification.integration.criteria.MovieFilterCriteria;
import com.cleverpine.specification.integration.entity.Actor;
import com.cleverpine.specification.integration.entity.Movie;
import com.cleverpine.specification.integration.expression.MovieTitleAndGenreSpecExpression;
import com.cleverpine.specification.item.FilterItem;
import com.cleverpine.specification.item.MultiFilterItem;
import com.cleverpine.specification.item.SingleFilterItem;
import com.cleverpine.specification.parser.SpecificationParserManager;
import com.cleverpine.specification.parser.json.FilterJsonArrayParser;
import com.cleverpine.specification.parser.json.SortJsonArrayParser;
import com.cleverpine.specification.producer.ComplexSpecificationProducer;
import com.cleverpine.specification.util.FilterOperator;
import com.cleverpine.specification.util.SortDirection;
import com.cleverpine.specification.util.SpecificationQueryConfig;
import com.cleverpine.specification.util.SpecificationRequest;
import com.cleverpine.specification.util.ValueConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.JoinType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpecificationProducerIT extends SpecificationProducerIntegrationTest {

    private static final ValueConverter VALUE_CONVERTER = new ValueConverter();

    private static final SpecificationQueryConfig<Movie> SPECIFICATION_QUERY_CONFIG;

    static {
        // @formatter:off
        SPECIFICATION_QUERY_CONFIG = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("movieTitle", "title")
                .addAttributePathMapping("genreName", "g.name")
                .addAttributePathMapping("actorFirstName", "a.filterName")
                .addAttributePathMapping("actorLastName", "a.lastName")
                .end()
                .build();
        // @formatter:on
    }

    private ComplexSpecificationProducer<Movie> movieSpecificationProducer;

    public SpecificationProducerIT() {
        super(SpecificationParserManager.builder()
                  .withSingleFilterParser(new FilterJsonArrayParser(new ObjectMapper()))
                  .withSingleSortParser(new SortJsonArrayParser(new ObjectMapper()))
                  .build(),
              VALUE_CONVERTER);
    }

    @BeforeEach
    void setUp() {
        movieSpecificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            SPECIFICATION_QUERY_CONFIG);
    }

    @Test
    void createSpecification_onInvalidFilterParam_shouldThrow() {
        String filterParam = "[[\"id\"]";

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        assertThrows(
            InvalidSpecificationException.class,
            () -> movieSpecificationProducer.createSpecification(specificationRequest)
        );
    }

    @Test
    void createSpecification_onInvalidFilterAttribute_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("invalid", "eq", "test")));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        assertThrows(
            InvalidSpecificationException.class,
            () -> movieSpecificationProducer.createSpecification(specificationRequest)
        );
    }

    @Test
    void findOne_onEqualForFlatEntityAttribute_shouldReturnValidResult() {
        String expectedMovieTitle = "Fast and Furious";
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("title", "eq", expectedMovieTitle)));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);
        Movie actual = findOne(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertEquals(expectedMovieTitle, actual.getTitle());
    }

    @Test
    void findOne_onEqualForEntityAttributeThatNotExists_shouldThrowException() {
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("movieTitle", "eq", "Fast and Furious")));

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        assertThrows(
            IllegalArgumentException.class,
            () -> findOne(movieSpecification, Movie.class)
        );
    }

    @Test
    void findOne_onEqualForEntityAttributeThatNotExistsButWithValidPathMapping_shouldReturnValidResult() {
        String expectedMovieTitle = "Fast and Furious";
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("movieTitle", "eq", expectedMovieTitle)));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);
        Movie actual = findOne(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertEquals(expectedMovieTitle, actual.getTitle());
    }

    @Test
    void findOne_onEqualForRelationalEntityAttributeAndNoJoinAndNoMappingToRelationalEntityIsPresent_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "eq", "Action")));

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        assertThrows(
            IllegalArgumentException.class,
            () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findOne_onEqualForRelationalEntityAttributeAndMappingToRelationalEntityIsPresentButNoJoin_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "eq", "Action")));

        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .attributePathConfig()
                .addAttributePathMapping("genreName", "g.name")
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        assertThrows(
            IllegalSpecificationException.class,
            () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findOne_onEqualForRelationalEntityAttributeAndJoinIsPresentButNoMappingToRelationalEntityAttribute_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "eq", "Action")));

        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        assertThrows(
            IllegalArgumentException.class,
            () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findAll_onEqualForRelationalEntityAttributeAndJoinAndMappingToRelationalEntityAttributeArePresent_shouldReturnValueResult() {
        String expectedGenre = "Action";
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "eq", expectedGenre)));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());

        Movie movie = actual.get(0);
        assertEquals(expectedGenre, movie.getGenre().getName());
    }

    @Test
    void findAll_onEqualForRelationalEntityAttributeButJoinDeclarationIsInvalid_shouldThrow() {
        String expectedGenre = "Action";
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "eq", expectedGenre)));

        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Actor.class, "genre", "g", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("genreName", "g.name")
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        assertThrows(
            IllegalSpecificationException.class,
            () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findAll_onNotEqualFilterOperator_shouldReturnValueResult() {
        String unexpectedGenre = "Action";
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "neq", unexpectedGenre)));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertNotEquals(unexpectedGenre, movie.getGenre().getName()));
    }

    @Test
    void findAll_onGreaterThanFilterOperator_shouldReturnValueResult() {
        Long expectedIdGreaterThan = 1L;
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("id", "gt", expectedIdGreaterThan.toString())));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() > expectedIdGreaterThan));
    }

    @Test
    void findAll_onLessThanFilterOperator_shouldReturnValueResult() {
        Long expectedIdLessThan = 2L;
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("id", "lt", expectedIdLessThan.toString())));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() < expectedIdLessThan));
    }

    @Test
    void findAll_onGreaterThanOrEqualFilterOperator_shouldReturnValueResult() {
        Long expectedIdGreaterThanOrEqual = 2L;
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("id", "gte", expectedIdGreaterThanOrEqual.toString())));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() >= expectedIdGreaterThanOrEqual));
    }

    @Test
    void findAll_onLessThanOrEqualFilterOperator_shouldReturnValueResult() {
        Long expectedIdLessThanOrEqual = 2L;
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("id", "lte", expectedIdLessThanOrEqual.toString())));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() <= expectedIdLessThanOrEqual));
    }

    @Test
    void findAll_onLikeFilterOperator_shouldReturnValueResult() {
        String expectedMovieTitleContains = "ast";
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("title", "like", expectedMovieTitleContains)));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getTitle().contains(expectedMovieTitleContains)));
    }

    @Test
    void findAll_onBetweenFilterOperator_shouldReturnValueResult() {
        Long firstValue = 2L;
        Long secondValue = 3L;

        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("id", "between", String.format("[\\\"%d\\\",\\\"%d\\\"]", firstValue, secondValue))));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie -> assertTrue(movie.getId() >= firstValue && movie.getId() <= secondValue));
    }

    @Test
    void findAll_onInFilterOperator_shouldReturnValueResult() {
        String firstMovie = "Fast and Furious";
        String secondMovie = "Deadpool";

        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("title", "in", String.format("[\\\"%s\\\",\\\"%s\\\"]", firstMovie, secondMovie))));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        actual.forEach(movie ->
                           assertTrue(movie.getTitle().equals(firstMovie) || movie.getTitle().equals(secondMovie)));
    }

    @Test
    void findAll_onDoubleEqualFilterItems_shouldReturnValueResult() {
        String expectedGenre = "Comedy";
        String expectedMovie = "Deadpool";

        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "eq", expectedGenre),
                List.of("title", "eq", expectedMovie)));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());

        Movie movie = actual.get(0);

        assertEquals(expectedGenre, movie.getGenre().getName());
        assertEquals(expectedMovie, movie.getTitle());
    }

    @Test
    void findAll_onSortForFlatEntityAttribute_shouldReturnValidResult() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .orderByConfig()
                .addOrderBy("title", SortDirection.DESC)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.createEmpty();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        List<Movie> expected = actual.stream()
            .sorted(Comparator.comparing(Movie::getTitle).reversed())
            .collect(Collectors.toList());

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void findAll_onSortForFilterAttributeThatDoesNotExist_shouldThrow() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .orderByConfig()
                .addOrderBy("invalidAttribute", SortDirection.DESC)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.createEmpty();

        assertThrows(
            InvalidSpecificationException.class,
            () -> specificationProducer.createSpecification(specificationRequest)
        );
    }

    @Test
    void findAll_onSortForNestedEntityAttributeAndJoinNotDefined_shouldThrow() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .attributePathConfig()
                .addAttributePathMapping("genreName", "g.name")
                .end()
                .orderByConfig()
                .addOrderBy("genreName", SortDirection.DESC)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.createEmpty();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        assertThrows(
            IllegalSpecificationException.class,
            () -> findAll(movieSpecification, Movie.class)
        );
    }

    @Test
    void findAll_onSortForNestedEntityAttributeAndJoinIsPresent_shouldReturnValidResult() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("genreName", "g.name")
                .end()
                .orderByConfig()
                .addOrderBy("genreName", SortDirection.DESC)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.createEmpty();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        List<Movie> expected = actual.stream()
            .sorted((first, second) -> second.getGenre().getName().compareTo(first.getGenre().getName()))
            .collect(Collectors.toList());

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void findAll_onSortAndFilterForNestedEntityAttribute_shouldReturnValidResult() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("genreName", "g.name")
                .end()
                .orderByConfig()
                .addOrderBy("genreName", SortDirection.DESC)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        List<FilterItem<Movie>> filterItems = List.of(new MultiFilterItem<>("genreName", FilterOperator.IN, List.of("Horror", "Comedy")));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterItems(filterItems)
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        List<Movie> expected = actual.stream()
            .sorted((first, second) -> second.getGenre().getName().compareTo(first.getGenre().getName()))
            .collect(Collectors.toList());

        assertArrayEquals(expected.toArray(), actual.toArray());
        actual.forEach(movie -> assertTrue(movie.getGenre().getName().equals("Horror") || movie.getGenre().getName().equals("Comedy")));
    }

    @Test
    void findAll_onComplexFilterAndSort_shouldReturnValidResult() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("actorFirstName", "a.firstName")
                .addAttributePathMapping("actorLastName", "a.lastName")
                .end()
                .orderByConfig()
                .addOrderBy("actorFirstName", SortDirection.DESC)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterItems(List.of(
                new SingleFilterItem<>("actorFirstName", FilterOperator.EQUAL, "Ryan"),
                new SingleFilterItem<>("actorLastName", FilterOperator.EQUAL, "Reynolds")))
            .build();

        CpSpecification<Movie> movieSpecification =
            specificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        actual.forEach(movie ->
                           assertTrue(movie.getActors().stream()
                                          .anyMatch(actor -> actor.getFirstName().equals("Ryan") && actor.getLastName().equals("Reynolds"))));
    }

    @Test
    void findAll_onNotDistinctEntity_shouldReturnDuplicates() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("actorFirstName", "a.firstName")
                .addAttributePathMapping("actorLastName", "a.lastName")
                .end()
                .orderByConfig()
                .addOrderBy("actorFirstName", SortDirection.DESC)
                .end()
                .entityDistinctRequired(false)
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterItems(List.of(
                new MultiFilterItem<>("actorFirstName", FilterOperator.IN, List.of("Ryan", "Morena"))))
            .build();

        CpSpecification<Movie> movieSpecification =
            specificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        Set<Long> movieIds = actual.stream().map(Movie::getId).collect(Collectors.toSet());

        assertTrue(actual.size() == movieIds.size());
    }

    @Test
    void findAll_onDistinctEntity_shouldReturnNoDuplicates() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "actors", "a", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("actorFirstName", "a.firstName")
                .addAttributePathMapping("actorLastName", "a.lastName")
                .end()
                .orderByConfig()
                .addOrderBy("actorFirstName", SortDirection.DESC)
                .end()
                .entityDistinctRequired(true)
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterItems(List.of(
                new MultiFilterItem<>("actorFirstName", FilterOperator.IN, List.of("Ryan", "Morena"))))
            .build();

        CpSpecification<Movie> movieSpecification =
            specificationProducer.createSpecification(specificationRequest);

        List<Movie> actual = findAll(movieSpecification, Movie.class);
        Set<Long> movieIds = actual.stream().map(Movie::getId).collect(Collectors.toSet());

        assertEquals(actual.size(), movieIds.size());
    }

    @Test
    void createSpecification_invalidFilterParam_shouldThrow() {
        String filterParam = "[[\"id\",\"=\"]]";
        String sortParam = createJsonArraySortParam(
            List.of(
                List.of("title", "desc")));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .withSortParam(sortParam)
            .build();

        assertThrows(
            InvalidSpecificationException.class,
            () -> movieSpecificationProducer.createSpecification(specificationRequest)
        );
    }

    @Test
    void createSpecification_invalidSortDirection_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("title", "=", "random")));
        String sortParam = "[[\"id\",\"=\"]]";

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .withSortParam(sortParam)
            .build();

        assertThrows(
            InvalidSpecificationException.class,
            () -> movieSpecificationProducer.createSpecification(specificationRequest)
        );
    }

    @Test
    void createSpecification_invalidSortParamNumber_shouldThrow() {
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("invalid", "=", "test")));
        String sortParam = "[[\"id\"]]";

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .withSortParam(sortParam)
            .build();

        assertThrows(
            InvalidSpecificationException.class,
            () -> movieSpecificationProducer.createSpecification(specificationRequest)
        );
    }

    @Test
    void findAll_whenFilterAndSort_shouldReturnValidResult() {
        String firstGenre = "Action";
        String secondGenre = "Comedy";
        String filterParam = createJsonArrayFilterParam(
            List.of(
                List.of("genreName", "in", String.format("[\\\"%s\\\",\\\"%s\\\"]", firstGenre, secondGenre))));

        String sortParam = createJsonArraySortParam(
            List.of(
                List.of("id", "desc")));

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterParam(filterParam)
            .withSortParam(sortParam)
            .build();

        CpSpecification<Movie> movieSpecification = movieSpecificationProducer.createSpecification(specificationRequest);
        List<Movie> actual = findAll(movieSpecification, Movie.class);
        Movie firstMovie = actual.get(0);
        Movie secondMovie = actual.get(1);

        assertTrue(firstMovie.getId() > secondMovie.getId());
        assertNotNull(actual);
        assertTrue(firstMovie.getGenre().getName().equals(firstGenre) || firstMovie.getGenre().getName().equals(secondGenre));
    }

    @Test
    void findAll_withDefaultFilterItems_shouldReturnValidResult() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("genreName", "g.name")
                .end()
                .filterConfig()
                .addFilter("genreName", FilterOperator.EQUAL, "Comedy")
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.createEmpty();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);
        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertEquals(2, actual.size());

        actual.forEach(movie -> assertEquals("Comedy", movie.getGenre().getName()));
    }

    @Test
    void findAll_onCustomAttributeExpression_shouldReturnValidResult() {
        // @formatter:off
        SpecificationQueryConfig<Movie> specificationQueryConfig = SpecificationQueryConfig.<Movie>builder()
                .joinConfig()
                .defineJoinClause(Movie.class, "genre", "g", JoinType.INNER)
                .end()
                .attributePathConfig()
                .addAttributePathMapping("genreName", "g.name")
                .end()
                .customExpressionConfig()
                .addCustomSpecificationExpression("titleGenreName", MovieTitleAndGenreSpecExpression.class)
                .end()
                .build();
        // @formatter:on

        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter,
            specificationQueryConfig);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterItems(List.of(new SingleFilterItem<>("titleGenreName", FilterOperator.EQUAL, "ITHorror")))
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);
        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertEquals(1, actual.size());

        Movie actualMovie = actual.get(0);

        assertEquals("IT", actualMovie.getTitle());
        assertEquals("Horror", actualMovie.getGenre().getName());
    }

    @Test
    void findAll_onStartsWithAttributeExpression_shouldReturnValidResult() {
        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterItems(List.of(new SingleFilterItem<>("title", FilterOperator.STARTS_WITH, "Deadpool")))
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);
        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertEquals(2, actual.size());

        Movie firstMovie = actual.get(0);
        Movie secondMovie = actual.get(1);

        assertEquals("Deadpool", firstMovie.getTitle());
        assertEquals("Deadpool 2", secondMovie.getTitle());
    }

    @Test
    void findAll_onEndsWithAttributeExpression_shouldReturnValidResult() {
        ComplexSpecificationProducer<Movie> specificationProducer = new ComplexSpecificationProducer<>(
            specificationParserManager,
            MovieFilterCriteria.class,
            valueConverter);

        SpecificationRequest<Movie> specificationRequest = SpecificationRequest.<Movie>builder()
            .withFilterItems(List.of(new SingleFilterItem<>("title", FilterOperator.ENDS_WITH, "Furious")))
            .build();

        CpSpecification<Movie> movieSpecification = specificationProducer.createSpecification(specificationRequest);
        List<Movie> actual = findAll(movieSpecification, Movie.class);

        assertEquals(1, actual.size());

        Movie actualMovie = actual.get(0);

        assertEquals("Fast and Furious", actualMovie.getTitle());
    }
}
