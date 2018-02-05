package com.qudini.exceptions;

import javax.annotation.Nonnull;

/**
 * Code paths can sometimes be asserted as never to run, yet the compiler is not convinced; this should be thrown in
 * those scenarios.
 * <p>
 * For example, Java requires all code paths to return a value in value-returning methods, even if a method call
 * <em>always</em> throws a runtime exception and therefore no return is needed. Throwing this exception makes sure
 * invalid code paths do not successfully run and documents that it <em>should</em> be dead code.
 */
public final class InvalidCodePathException extends IllegalStateException {
    private static final String MESSAGE = "a code path that should never be executed was executed";

    public InvalidCodePathException() {
        super(MESSAGE);
    }

    public InvalidCodePathException(@Nonnull final Throwable cause) {
        super(MESSAGE, cause);
    }
}
