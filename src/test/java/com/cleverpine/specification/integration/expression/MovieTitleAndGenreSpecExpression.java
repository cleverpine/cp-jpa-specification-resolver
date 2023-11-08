package com.cleverpine.specification.integration.expression;

import com.cleverpine.specification.expression.SpecificationExpression;
import com.cleverpine.specification.integration.entity.Movie;
import com.cleverpine.specification.util.QueryContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public class MovieTitleAndGenreSpecExpression extends SpecificationExpression<Movie, String> {

    public MovieTitleAndGenreSpecExpression(String attributePath, QueryContext<Movie> queryContext) {
        super(attributePath, queryContext);
    }

    @Override
    public Expression<String> produceExpression(Root<Movie> root, CriteriaBuilder criteriaBuilder) {
        Path<String> title = buildPathExpressionToEntityAttribute("title", root);
        Path<String> genreName = buildPathExpressionToEntityAttribute("g.name", root);
        return criteriaBuilder.concat(title, genreName);
    }
}
