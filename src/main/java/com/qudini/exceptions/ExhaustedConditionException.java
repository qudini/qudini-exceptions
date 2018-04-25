package com.qudini.exceptions;

public final class ExhaustedConditionException extends RuntimeException {
    public ExhaustedConditionException() {
    }

    public ExhaustedConditionException(String message) {
        super(message);
    }

    public ExhaustedConditionException(Exception exception) {
        super(exception);
    }

    public ExhaustedConditionException(String message, Exception exception) {
        super(message, exception);
    }
}
