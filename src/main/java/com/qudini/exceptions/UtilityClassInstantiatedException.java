package com.qudini.exceptions;

public final class UtilityClassInstantiatedException extends IllegalStateException {
    private static final String MESSAGE = "A utility class that should not be instantiated.";

    public UtilityClassInstantiatedException() {
        super(MESSAGE);
    }
}
