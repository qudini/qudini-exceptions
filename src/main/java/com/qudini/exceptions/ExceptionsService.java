package com.qudini.exceptions;


import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

/**
 * Utilities for handling exceptions. These include:
 * <ul>
 * <li>Turning checked exceptions into unchecked ones.</li>
 * <li>Converting all checked exceptions thrown in a code block to unchecked.</li>
 * <li>
 * Reporting exceptions through application-specific error-reporting tools, but continuing as usual afterwards
 * </li>
 * <li>
 * Reporting exceptions through application-specific error-reporting tools, and then rethrowing the exception.
 * </li>
 * </ul>
 */
@CheckReturnValue
public class ExceptionsService {

    private final Set<Class<? extends Exception>> exceptionsToIgnore;
    private final Set<? extends Reporter> reporters;

    /**
     * @return exception utilities that work on all exceptions derived from `java.lang.Exception`, except for
     * {@code exceptionsToIgnore}. Ignored exceptions work as if these utilities were not used at all; for example,
     * {@link #reportQuietly} and {@link #reportAndRethrow} will not report these and just keep throwing the exception
     * as normal.
     * <p>
     * Features that use reporting, such as {@link #reportAndRethrow(PotentiallyErroneousWithoutResult)}, will use the
     * provided reporters. These can, for example, use specific logging or report to external error reporting services.
     */
    public ExceptionsService(Set<Class<? extends Exception>> exceptionsToIgnore, Set<? extends Reporter> reporters) {
        this.exceptionsToIgnore = exceptionsToIgnore;
        this.reporters = reporters;
    }

    /**
     * Throws an exception at runtime, even if it's compile-checked.
     * <p>
     * This is useful for using APIs which misuse compile-checked exceptions, forcing its consumers to code verbosely
     * and to leak implementation details. This also alleviates the annoying interplay between lambdas and
     * compile-checked exceptions.
     */
    public void throwUnchecked(final Exception exception) {
        try {
            throw exception;
        } catch (final RuntimeException rethrownException) {
            throw rethrownException;
        } catch (final Exception rethrownException) {
            throw new RuntimeCheckedException(rethrownException);
        }
    }

    /**
     * Runs a block of code in which compile-time exceptions are converted to runtime exceptions.
     */
    public <T> T unchecked(PotentiallyErroneous<T> f) {
        try {
            return f.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception t) {
            throw new RuntimeCheckedException(t);
        }
    }

    /**
     * Runs a block of code in which compile-time exceptions are converted to runtime exceptions.
     */
    public void unchecked(PotentiallyErroneousWithoutResult f) {
        try {
            f.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception t) {
            throw new RuntimeCheckedException(t);
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
    public <A> Optional<A> reportQuietly(PotentiallyErroneous<A> f) {
        try {
            return Optional.of(f.run());
        } catch (Exception e) {
            if (toBeBypassed(e)) {
                throwUnchecked(e);
                throw new InvalidCodePathException();
            } else {
                reporters.forEach(reporter -> reporter.report(e));
                return Optional.empty();
            }
        }
    }

    /**
     * @see #reportQuietly(PotentiallyErroneous)
     */
    public void reportQuietly(PotentiallyErroneousWithoutResult f) {

        // The only case where @CheckReturnValue should be ignored for `#reportQuietly`.
        reportQuietly(() -> {
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
    public <A> A reportAndRethrow(PotentiallyErroneous<A> f) {
        try {
            return f.run();
        } catch (Exception e) {
            if (!toBeBypassed(e)) {
                reporters.forEach(reporter -> reporter.report(e));
            }
            throwUnchecked(e);
            throw new InvalidCodePathException();
        }
    }

    /**
     * @see #reportAndRethrow(PotentiallyErroneous)
     */
    public void reportAndRethrow(PotentiallyErroneousWithoutResult f) {

        // The only case where @CheckReturnValue should be ignored for `#reportAndRethrow`.
        reportAndRethrow(() -> {
            f.run();

            // A silly throwaway value, since the only valid value of Void is null, which crashes `Optional#of`.
            return Boolean.TRUE;
        });
    }

    private boolean toBeBypassed(Exception exception) {
        return exceptionsToIgnore
                .stream()
                .anyMatch(toIgnore -> toIgnore.isInstance(exception));
    }

    /**
     * @see #reportQuietly(PotentiallyErroneous)
     */
    @FunctionalInterface
    public interface PotentiallyErroneous<A> {

        @CheckReturnValue
        A run() throws Exception;
    }

    /**
     * @see #reportQuietly(PotentiallyErroneousWithoutResult)
     */
    @FunctionalInterface
    public interface PotentiallyErroneousWithoutResult {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface Reporter {
        void report(String message, Exception cause);

        default void report(Exception cause) {
            report(cause.getMessage(), cause);
        }
    }
}
