package com.qudini.exceptions;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionsTest {

    @Test
    public void throwUnchecked() {
        try {
            Exceptions.throwUnchecked(new Exception());
        } catch (RuntimeException exception) {
            return;
        }
        fail();
    }

    @Test
    public void unchecked() {
        try {
            Exceptions.unchecked((Exceptions.PotentiallyErroneousWithoutResult) () -> {
                throw new ExcludedException2();
            });

            fail();
        } catch (Exception exception) {
            assertTrue(exception.getCause() instanceof ExcludedException2);
        }

        try {
            Exceptions.unchecked(() -> {
                throw new UnsupportedOperationException();
            });

            fail();
        } catch (UnsupportedOperationException exception) {
        }
    }

    @Test
    public void reportQuietly() {
        Exceptions.reportQuietly(emptyList(), () -> {
            throw new Exception();
        });
    }

    @Test
    public void reportAndRethrow() {
        AtomicInteger reportCount = new AtomicInteger();
        List<Exceptions.Reporter> reporters = singletonList((e, m) -> reportCount.incrementAndGet());

        try {
            Exceptions.reportAndRethrow(
                    reporters,
                    () -> {
                        throw new UnsupportedOperationException();
                    }
            );
            fail();
        } catch (UnsupportedOperationException exception) {
            assertEquals(reportCount.get(), 1);
        }
    }

    private final class ExcludedException2 extends Exception {
    }
}
