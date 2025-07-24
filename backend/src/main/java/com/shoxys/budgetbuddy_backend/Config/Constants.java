package com.shoxys.budgetbuddy_backend.Config;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Centralized constants for BudgetBuddy backend to ensure consistency across the application.
 */
public final class Constants {

    // Private constructor to prevent instantiation
    private Constants() {
    }

    // API-related constants
    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_ENDPOINT = API_BASE_PATH + "/auth";
    public static final String ACCOUNT_ENDPOINT = API_BASE_PATH + "/account";
    public static final String UPLOAD_ENDPOINT = API_BASE_PATH + "/upload";
    public static final String DASHBOARD_ENDPOINT = API_BASE_PATH + "/dashboard";
    public static final String TRANSACTION_ENDPOINT = API_BASE_PATH + "/transactions";
    public static final String SAVING_GOALS_ENDPOINT = API_BASE_PATH + "/saving-goals";
    public static final String SETTINGS_ENDPOINT = API_BASE_PATH + "/settings";

    // JWT configuration
    public static final String JWT_ISSUER = "BudgetBuddy";
    public static final long JWT_EXPIRATION_DAYS = 7; // 7 Days
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_COOKIE_NAME = "jwt";
    public static final String COOKIE_PATH = "/";
    public static final long COOKIE_MAX_AGE_SECONDS = 7 * 24 * 60 * 60; // 7 days
    public static final String COOKIE_SAME_SITE = "Lax";
    public static final boolean COOKIE_HTTP_ONLY = true;
    public static final boolean COOKIE_SECURE = false; // Set to true in production with HTTPS

    // CSV processing
    public static final String CSV_DELIMITER = ",";
    public static final String CSV_CONTENT_TYPE = "text/csv";
    public static final String CSV_FILE_EXTENSION = ".csv";
    public static final List<DateTimeFormatter> CSV_DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("d MMM yy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d-MM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH)
    );

    // Validation constraints
    public static final String MIN_BALANCE = "0.01";
    public static final int MAX_BALANCE_INTEGER_DIGITS = 12;
    public static final int MAX_BALANCE_FRACTION_DIGITS = 2;
    public static final int MAX_ACCOUNT_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final int MAX_DESCRIPTION_LENGTH = 255;
    public static final int MAX_CATEGORY_LENGTH = 50;
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_IMAGE_REF_LENGTH = 255;
    public static final int MAX_ERROR_MESSAGE_LENGTH = 500;

    // Exception messages
    public static final String ACCOUNT_NOT_FOUND = "Account not found for specified user";
    public static final String EMAIL_EXISTS = "Email already exists";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String MISSING_FIELD = "Missing required field";
    public static final String PASSWORD_MISMATCH = "Password mismatch";
    public static final String SAVING_GOAL_NOT_FOUND = "Saving goal not found";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
}