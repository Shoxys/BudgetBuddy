package com.shoxys.budgetbuddy_backend;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("Utils is a utility class and cannot be instantiated");
    }

    // Static helper method to check if a String is null or empty
    public static boolean nullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
