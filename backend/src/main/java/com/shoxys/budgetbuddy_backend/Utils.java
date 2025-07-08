package com.shoxys.budgetbuddy_backend;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("Utils is a utility class and cannot be instantiated");
    }

    // Check if a String is null or empty
    public static boolean nullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // Check if a list is null or empty
    public static boolean nullOrEmpty(List<?>  list) {
        return list == null || list.isEmpty();
    }

    // Check if a BigDecimal is null or negative
    public static boolean nullOrNegative(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public static LocalDate getStartOfWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        return today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
    }

    public static LocalDate getEndOfWeek() {
        LocalDate startOfWeek = getStartOfWeek();
        return startOfWeek.plusDays(6);
    }

}
