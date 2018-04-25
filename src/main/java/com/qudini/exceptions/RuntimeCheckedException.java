package com.qudini.exceptions;

/**
 * Wraps a checked exception in a runtime exception to avoid the need for throws clauses.
 */
public final class RuntimeCheckedException extends RuntimeException {

    /**
     * Wraps a compile checked exception.
     *
     * @param exception An exception that isn't a RuntimeException.
     * @throws IllegalArgumentException If {@code exception} is a RuntimeException.
     */
    public RuntimeCheckedException(Exception exception) {
        super(exception);
        if (exception instanceof RuntimeException) {
            throw new IllegalArgumentException("only checked exceptions can be wrapped in `RuntimeCheckedException`s");
        }
    }
}
