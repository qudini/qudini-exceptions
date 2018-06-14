package com.qudini.exceptions;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionsServiceTest {

    private static final Set<Class<? extends Exception>> excludedExceptions = new HashSet<>(asList(
            ExcludedException1.class,
            ExcludedException2.class
    ));

    private ExceptionsService exceptionsServiceForAll = new ExceptionsService(emptySet(), emptySet());
    private ExceptionsService exceptionsServiceExcluding = new ExceptionsService(excludedExceptions, emptySet());

    @Test
    public void throwUnchecked() {
        try {
            exceptionsServiceForAll.throwUnchecked(new Exception());
        } catch (RuntimeException exception) {
            return;
        }
        fail();
    }

    @Test
    public void unchecked() {
        try {
            exceptionsServiceForAll.unchecked((ExceptionsService.PotentiallyErroneousWithoutResult) () -> {
                throw new ExcludedException2();
            });

            fail();
        } catch (Exception exception) {
            assertTrue(exception.getCause() instanceof ExcludedException2);
        }

        try {
            exceptionsServiceForAll.unchecked(() -> {
                throw new UnsupportedOperationException();
            });

            fail();
        } catch (UnsupportedOperationException exception) {
        }
    }

    @Test
    public void reportQuietly() {
        exceptionsServiceForAll.reportQuietly(() -> {
            throw new Exception();
        });

        try {
            exceptionsServiceExcluding.reportQuietly(() -> {
                throw new ExcludedException1();
            });
            fail();
        } catch (ExcludedException1 exception) {
        }
    }

    @Test
    public void reportAndRethrow() {
        AtomicInteger reportCount = new AtomicInteger();
        Set<ExceptionsService.Reporter> reporters = singleton((e, m) -> reportCount.incrementAndGet());

        try {
            new ExceptionsService(emptySet(), reporters).reportAndRethrow(() -> {
                throw new UnsupportedOperationException();
            });
            fail();
        } catch (UnsupportedOperationException exception) {
            assertEquals(reportCount.get(), 1);
        }

        try {
            new ExceptionsService(excludedExceptions, reporters).reportAndRethrow(() -> {
                throw new ExcludedException1();
            });
            fail();
        } catch (ExcludedException1 exception) {
            assertEquals(reportCount.get(), 1);
        }
    }

    private final class ExcludedException1 extends RuntimeException {
    }

    private final class ExcludedException2 extends Exception {
    }
}
