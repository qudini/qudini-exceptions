package com.qudini.exceptions;

public final class ExhaustedConditionException extends RuntimeException {
    public ExhaustedConditionException() {
    }

    public ExhaustedConditionException(String message) {
        super(message);
    }

    public ExhaustedConditionException(Throwable throwable) {
        super(throwable);
    }

    public ExhaustedConditionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
