package com.cleverpine.specification.util;

import com.cleverpine.specification.core.Between;
import com.cleverpine.specification.core.CpSpecification;
import com.cleverpine.specification.core.EndsWith;
import com.cleverpine.specification.core.Equals;
import com.cleverpine.specification.core.GreaterThan;
import com.cleverpine.specification.core.GreaterThanOrEquals;
import com.cleverpine.specification.core.In;
import com.cleverpine.specification.core.LessThan;
import com.cleverpine.specification.core.LessThanOrEquals;
import com.cleverpine.specification.core.Like;
import com.cleverpine.specification.core.NotEquals;
import com.cleverpine.specification.core.StartsWith;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.cleverpine.specification.util.FilterConstants.BETWEEN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.ENDS_WITH_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.EQUAL_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.GREATER_THAN_EQUAL_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.GREATER_THAN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.IN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.LESS_THAN_EQUAL_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.LESS_THAN_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.LIKE_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.NOT_EQUAL_OPERATOR;
import static com.cleverpine.specification.util.FilterConstants.STARTS_WITH_OPERATOR;

/**
 * The {@link FilterOperator} enumeration defines different types of operators that can be used in a filter
 * expression. Each operator has a unique value, a corresponding {@link com.cleverpine.specification.core.CpSpecification} implementation
 * class, and a flag indicating if it requires a single filter value or a range of filter values.
 */
@RequiredArgsConstructor
@Getter
public enum FilterOperator {

    EQUAL(EQUAL_OPERATOR, Equals.class, true),
    NOT_EQUAL(NOT_EQUAL_OPERATOR, NotEquals.class, true),
    GREATER_THAN(GREATER_THAN_OPERATOR, GreaterThan.class, true),
    LESS_THAN(LESS_THAN_OPERATOR, LessThan.class, true),
    GREATER_THAN_EQUAL(GREATER_THAN_EQUAL_OPERATOR, GreaterThanOrEquals.class, true),
    LESS_THAN_EQUAL(LESS_THAN_EQUAL_OPERATOR, LessThanOrEquals.class, true),
    LIKE(LIKE_OPERATOR, Like.class, true),
    STARTS_WITH(STARTS_WITH_OPERATOR, StartsWith.class, true),
    ENDS_WITH(ENDS_WITH_OPERATOR, EndsWith.class, true),
    BETWEEN(BETWEEN_OPERATOR, Between.class, false),
    IN(IN_OPERATOR, In.class, false);

    private static final Map<String, FilterOperator> VALUES = new HashMap<>();

    static {
        Arrays.stream(FilterOperator.values())
            .forEach(filterOperator -> VALUES.put(filterOperator.getValue(), filterOperator));
    }

    private final String value;

    private final Class<? extends CpSpecification> specificationType;

    private final boolean singleFilterValue;

    /**
     * Returns the {@link FilterOperator} enumeration value corresponding to the provided operator value.
     *
     * @param value The operator value to find the corresponding enumeration value for.
     * @return An {@link Optional} object containing the corresponding {@link FilterOperator} value if one exists,
     * or an empty {@link Optional} object if one does not.
     */
    public static Optional<FilterOperator> getByValue(String value) {
        return Optional.ofNullable(VALUES.get(value));
    }

}
