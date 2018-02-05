package com.qudini.exceptions;

/**
 * Illegal state has occurred in the persistent data model.
 */
public final class IllegalDataModelStateException extends RuntimeException {
    public IllegalDataModelStateException() {
        super();
    }

    public IllegalDataModelStateException(final Throwable cause) {
        super(cause);
    }

    public IllegalDataModelStateException(final String message) {
        super(message);
    }

    public IllegalDataModelStateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
