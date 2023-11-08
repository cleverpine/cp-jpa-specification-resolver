package com.cleverpine.specification.util;

public final class FilterConstants {

    private FilterConstants() {
        throw new AssertionError("Cannot create instances of this class");
    }

    // Operators
    public static final String EQUAL_OPERATOR = "eq";

    public static final String NOT_EQUAL_OPERATOR = "neq";

    public static final String GREATER_THAN_OPERATOR = "gt";

    public static final String LESS_THAN_OPERATOR = "lt";

    public static final String GREATER_THAN_EQUAL_OPERATOR = "gte";

    public static final String LESS_THAN_EQUAL_OPERATOR = "lte";

    public static final String LIKE_OPERATOR = "like";

    public static final String STARTS_WITH_OPERATOR = "sw";

    public static final String ENDS_WITH_OPERATOR = "ew";

    public static final String IN_OPERATOR = "in";

    public static final String BETWEEN_OPERATOR = "between";

    public static final String INVALID_FILTER_ARGS_COUNT = "A single filter item should be [%d] arguments long";

    public static final String INVALID_SORT_ARGS_COUNT = "A single sort item should be [%d] arguments long";

    public static final String INVALID_FILTER_OPERATOR = "Operator [%s] is not supported";

    public static final String INVALID_SORT_DIRECTION = "Direction [%s] is not supported";

    public static final String INVALID_FILTER_QUERY_PARAMETER = "Invalid filter query parameter format. [%s] type required";

    public static final String INVALID_SORT_QUERY_PARAMETER = "Invalid sort query parameter format. [%s] type required";

    public static final String INVALID_FILTER_ATTRIBUTE = "Invalid attribute [%s] in [%s] filter type";

    public static final String EMPTY_FILTER_ATTRIBUTE = "There is an empty filter attribute";

    public static final String INVALID_VALUE_FOR_CLASS = "Invalid value [%s] for type [%s]";

    public static final String INVALID_SPECIFICATION_CREATION = "Specification [%s] cannot be instantiated";

    public static final String INVALID_EXPRESSION_CREATION = "Expression [%s] cannot be instantiated";

    public static final String INVALID_VALUES_COUNT = "[%s] filter item should have [%d] parameters";

    public static final String JOIN_NOT_DEFINED = "Join with alias [%s] is not defined";

    public static final String INVALID_JOIN = "Cannot join [%s] from [%s] entity. Entity [%s] is defined for this join";

    public static final String NULL_VALUE = "This value cannot be converted, because it is null";

    public static final String ENTITY_ATTRIBUTE_SEPARATOR = ".";

    public static final String EMPTY_STRING = "";

    public static final String PARSER_NOT_PROVIDED = "The filter/sort parameter is provided, but a parser is not found";

    public static final Integer VALID_FILTER_ARGS_COUNT = 3;

    public static final Integer VALID_SORT_ARGS_COUNT = 2;
}
