package com.qudini.exceptions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptySet;

/**
 * @deprecated Use {@link ExceptionsService} instead.
 * <p>
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
@Deprecated
@CheckReturnValue
public final class Exceptions {

    private Exceptions() {
        throw new UtilityClassInstantiatedException();
    }

    private static final ExceptionsService exceptionsService = new ExceptionsService(emptySet(), emptySet());

    /**
     * @deprecated Use {@link ExceptionsService#throwUnchecked(Exception)} instead.
     * <p>
     * Throws an exception at runtime, even if it's compile-checked.
     * <p>
     * This is useful for using APIs which misuse compile-checked exceptions, forcing its consumers to code verbosely
     * and to leak implementation details. This also alleviates the annoying interplay between lambdas and
     * compile-checked exceptions.
     */
    @Deprecated
    public static void throwUnchecked(final Exception exception) {
        exceptionsService.throwUnchecked(exception);
    }

    /**
     * @deprecated Use {@link ExceptionsService#unchecked(ExceptionsService.PotentiallyErroneous)} instead.
     * <p>
     * Runs a block of code in which compile-time exceptions are converted to runtime exceptions.
     */
    @Deprecated
    public static <T> T unchecked(PotentiallyErroneous<T> f) {
        return exceptionsService.unchecked(f);
    }

    /**
     * @deprecated Use {@link ExceptionsService#unchecked(ExceptionsService.PotentiallyErroneousWithoutResult)} instead.
     * <p>
     * Runs a block of code in which compile-time exceptions are converted to runtime exceptions.
     */
    @Deprecated
    public static void unchecked(PotentiallyErroneousWithoutResult f) {
        exceptionsService.unchecked(f);
    }

    /**
     * @deprecated Use {@link ExceptionsService#reportQuietly(ExceptionsService.PotentiallyErroneous)} instead.
     * <p>
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
    @Deprecated
    @Nonnull
    public static <A> Optional<A> reportQuietly(List<Reporter> reporters, PotentiallyErroneous<A> f) {
        return new ExceptionsService(emptySet(), new HashSet<>(reporters)).reportQuietly(f);
    }

    /**
     * @deprecated Use
     * {@link ExceptionsService#reportQuietly(ExceptionsService.PotentiallyErroneousWithoutResult)} instead.
     * <p>
     * @see #reportQuietly(List, PotentiallyErroneous)
     */
    @Deprecated
    public static void reportQuietly(List<Reporter> reporters, PotentiallyErroneousWithoutResult f) {
        new ExceptionsService(emptySet(), new HashSet<>(reporters)).reportQuietly(f);
    }

    /**
     * @deprecated Use {@link ExceptionsService#reportAndRethrow(ExceptionsService.PotentiallyErroneous)} instead.
     * <p>
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
    @Deprecated
    @Nonnull
    public static <A> A reportAndRethrow(List<Reporter> reporters, PotentiallyErroneous<A> f) {
        return new ExceptionsService(emptySet(), new HashSet<>(reporters)).reportAndRethrow(f);
    }

    /**
     * @deprecated Use
     * {@link ExceptionsService#reportAndRethrow(ExceptionsService.PotentiallyErroneousWithoutResult)} instead.
     * <p>
     * @see #reportAndRethrow(List, PotentiallyErroneous)
     */
    @Deprecated
    public static void reportAndRethrow(List<Reporter> reporters, PotentiallyErroneousWithoutResult f) {
        new ExceptionsService(emptySet(), new HashSet<>(reporters)).reportAndRethrow(f);
    }

    /**
     * @deprecated Use {@link ExceptionsService.PotentiallyErroneous} instead.
     * <p>
     * @see #reportQuietly(List, PotentiallyErroneous)
     */
    @Deprecated
    @FunctionalInterface
    public interface PotentiallyErroneous<T> extends ExceptionsService.PotentiallyErroneous<T> {
    }

    /**
     * @deprecated Use {@link ExceptionsService.PotentiallyErroneousWithoutResult} instead.
     * <p>
     * @see #reportQuietly(List, PotentiallyErroneousWithoutResult)
     */
    @Deprecated
    @FunctionalInterface
    public interface PotentiallyErroneousWithoutResult extends ExceptionsService.PotentiallyErroneousWithoutResult {
    }

    /**
     * @deprecated Use {@link ExceptionsService.Reporter} instead.
     */
    @Deprecated
    @FunctionalInterface
    public interface Reporter extends ExceptionsService.Reporter {
    }
}
