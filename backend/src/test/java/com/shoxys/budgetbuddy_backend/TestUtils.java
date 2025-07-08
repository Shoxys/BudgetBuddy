package com.shoxys.budgetbuddy_backend;

import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestUtils {
    private TestUtils() {
        throw new UnsupportedOperationException("Utils is a utility class and cannot be instantiated");
    }

    public static <T> void assertListElementsMatch(
            List<T> expected,
            List<T> actual,
            BiConsumer<T, T> fieldAsserter
    ) {
        assertEquals(expected.size(), actual.size(), "List sizes must match");

        for (int i = 0; i < expected.size(); i++) {
            fieldAsserter.accept(expected.get(i), actual.get(i));
        }
    }
}
