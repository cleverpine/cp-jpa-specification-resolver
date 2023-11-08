package com.cleverpine.specification.exception;

/**
 * Thrown to indicate an invalid specification error.
 * It is used mostly for invalid data errors.
 */
public class InvalidSpecificationException extends RuntimeException {

    /**
     * Constructs a {@link InvalidSpecificationException} with the specified
     * detail message.
     *
     * @param message the detail message.
     */
    public InvalidSpecificationException(String message) {
        super(message);
    }

}
