package com.qudini.exceptions;

public final class ExhaustedEnumVariantsException extends RuntimeException {

    private final Enum illegalVariant;

    public <A extends Enum<A>> ExhaustedEnumVariantsException(A illegalVariant) {
        this.illegalVariant = illegalVariant;
    }

    public ExhaustedEnumVariantsException(String message) {
        super(message);
        this.illegalVariant = null;
    }

    public <A extends Enum<A>> ExhaustedEnumVariantsException(A illegalVariant, String message) {
        super(message);
        this.illegalVariant = illegalVariant;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "; unhandled variant: " + getIllegalVariant();
    }

    public Enum getIllegalVariant() {
        return illegalVariant;
    }
}
