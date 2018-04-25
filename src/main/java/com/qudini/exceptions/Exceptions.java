package com.qudini.exceptions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@CheckReturnValue
public final class Exceptions {

    private Exceptions() {
        throw new UtilityClassInstantiatedException();
    }

    /**
     * Throws an exception at runtime, even if it's compile-checked.
     * <p>
     * This is useful for using APIs which misuse compile-checked exceptions, forcing its consumers to code verbosely
     * and to leak implementation details. This also alleviates the annoying interplay between lambdas and
     * compile-checked exceptions.
     */
    public static void throwUnchecked(final Throwable throwable) {
        try {
            throw throwable;
        } catch (final RuntimeException | Error rethrownException) {
            throw rethrownException;
        } catch (final Throwable rethrownException) {
            throw new RuntimeException(rethrownException);
        }
    }

    /**
     * Runs a block of code in which compile-time exceptions are converted to runtime exceptions.
     */
    public static <T> T unchecked(PotentiallyErroneous<T> f) {
        try {
            return f.run();
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Runs a block of code in which compile-time exceptions are converted to runtime exceptions.
     */
    public static void unchecked(PotentiallyErroneousWithoutResult f) {
        try {
            f.run();
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Report errors but continue without throwing an exception. This is designed for events whose breakages should be
     * logged, but should not break execution flow.
     * <p>
     * Eg.:
     * <pre>{@code
     * return Exceptions.reportQuietly(
     *         Arrays.asList(
     *                 new NewRelicReporter(),
     *                 (message, exception) -> myOwnHandler(exception)
     *         ),
     *         () -> {
     *             doSomethingThatMayCrash1();
     *             doSomethingThatMayCrash2();
     *             return successfulResult;
     *         }
     * );
     * }</pre>
     * <p>
     */
    @Nonnull
    public static <A> Optional<A> reportQuietly(List<Reporter> reporters, PotentiallyErroneous<A> f) {
        try {
            return Optional.of(f.run());
        } catch (Throwable t) {
            reporters.forEach(reporter -> reporter.report(t));
            return Optional.empty();
        }
    }

    /**
     * @see #reportQuietly(List, PotentiallyErroneous)
     */
    public static void reportQuietly(List<Reporter> reporters, PotentiallyErroneousWithoutResult f) {

        // The only case where @CheckReturnValue should be ignored for `#reportQuietly`.
        reportQuietly(reporters, () -> {
            f.run();

            // A silly throwaway value, since the only valid value of Void is null, which crashes `Optional#of`.
            return Boolean.TRUE;
        });
    }

    /**
     * Report errors and then continue throwing the exception. This is designed for exceptions that we want explicitly
     * to be logged to services like NewRelic.
     * <p>
     * Eg.:
     * <pre>{@code
     * return Exceptions.reportAndRethrow(
     *         Arrays.asList(
     *                 new NewRelicReporter(),
     *                 (message, exception) -> myOwnHandler(exception)
     *         ),
     *         () -> {
     *             doSomethingThatMayCrash1();
     *             doSomethingThatMayCrash2();
     *             return successfulResult;
     *         }
     * );
     * }</pre>
     */
    @Nonnull
    public static <A> A reportAndRethrow(List<Reporter> reporters, PotentiallyErroneous<A> f) {
        try {
            return f.run();
        } catch (Throwable t) {
            reporters.forEach(reporter -> reporter.report(t));
            throwUnchecked(t);
            throw new InvalidCodePathException();
        }
    }

    /**
     * @see #reportAndRethrow(List, PotentiallyErroneous)
     */
    public static void reportAndRethrow(List<Reporter> reporters, PotentiallyErroneousWithoutResult f) {

        // The only case where @CheckReturnValue should be ignored for `#reportAndRethrow`.
        reportAndRethrow(reporters, () -> {
            f.run();

            // A silly throwaway value, since the only valid value of Void is null, which crashes `Optional#of`.
            return Boolean.TRUE;
        });
    }

    /**
     * @see #reportQuietly(List, PotentiallyErroneous)
     */
    @FunctionalInterface
    public interface PotentiallyErroneous<A> {

        @CheckReturnValue
        A run() throws Throwable;
    }

    /**
     * @see #reportQuietly(List, PotentiallyErroneousWithoutResult)
     */
    @FunctionalInterface
    public interface PotentiallyErroneousWithoutResult {
        void run() throws Throwable;
    }

    @FunctionalInterface
    public interface Reporter {
        void report(String message, Throwable cause);

        default void report(Throwable cause) {
            report(cause.getMessage(), cause);
        }
    }
}
