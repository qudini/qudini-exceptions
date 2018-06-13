package com.qudini.exceptions;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionsServiceTest {

    private ExceptionsService exceptionsServiceForAll = ExceptionsService.forAll();
    private ExceptionsService exceptionsServiceExcluding = ExceptionsService.bypassing(
            ExcludedException1.class,
            ExcludedException2.class
    );

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
        exceptionsServiceForAll.reportQuietly(emptyList(), () -> {
            throw new Exception();
        });

        try {
            exceptionsServiceExcluding.reportQuietly(emptyList(), () -> {
                throw new ExcludedException1();
            });
            fail();
        } catch (ExcludedException1 exception) {
        }
    }

    @Test
    public void reportAndRethrow() {
        AtomicInteger reportCount = new AtomicInteger();
        List<ExceptionsService.Reporter> reporters = singletonList((e, m) -> reportCount.incrementAndGet());

        try {
            exceptionsServiceForAll.reportAndRethrow(
                    reporters,
                    () -> {
                        throw new UnsupportedOperationException();
                    }
            );
            fail();
        } catch (UnsupportedOperationException exception) {
            assertEquals(reportCount.get(), 1);
        }

        try {
            exceptionsServiceExcluding.reportAndRethrow(
                    reporters,
                    () -> {
                        throw new ExcludedException1();
                    }
            );
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
