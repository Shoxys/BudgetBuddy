package com.shoxys.budgetbuddy_backend.Utils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class providing common validation and date-related methods for the application. */
public final class Utils {
  private static final Logger logger = LoggerFactory.getLogger(Utils.class);
  private static final String EMAIL_REGEX =
      "^(?=.{1,64}@)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

  private Utils() {
    logger.error("Attempted to instantiate Utils class");
    throw new UnsupportedOperationException("Utils is a utility class and cannot be instantiated");
  }

  /**
   * Checks if a string is null or empty.
   *
   * @param str the string to check
   * @return true if the string is null or empty, false otherwise
   */
  public static boolean nullOrEmpty(String str) {
    logger.debug("Checking if string is null or empty: {}", str);
    boolean result = str == null || str.isEmpty();
    logger.debug("String null/empty check result: {}", result);
    return result;
  }

  /**
   * Checks if a list is null or empty.
   *
   * @param list the list to check
   * @return true if the list is null or empty, false otherwise
   */
  public static boolean nullOrEmpty(List<?> list) {
    logger.debug("Checking if list is null or empty, size: {}", list != null ? list.size() : null);
    boolean result = list == null || list.isEmpty();
    logger.debug("List null/empty check result: {}", result);
    return result;
  }

  /**
   * Checks if a BigDecimal is null or negative.
   *
   * @param amount the BigDecimal to check
   * @return true if the BigDecimal is null or negative, false otherwise
   */
  public static boolean nullOrNegative(BigDecimal amount) {
    logger.debug("Checking if BigDecimal is null or negative: {}", amount);
    boolean result = amount == null || amount.compareTo(BigDecimal.ZERO) < 0;
    logger.debug("BigDecimal null/negative check result: {}", result);
    return result;
  }

  /**
   * Retrieves the start date of the current week (Monday).
   *
   * @return the LocalDate representing the start of the week
   */
  public static LocalDate getStartOfWeek() {
    LocalDate today = LocalDate.now();
    DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
    LocalDate start = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
    logger.debug("Calculated start of week: {}", start);
    return start;
  }

  /**
   * Retrieves the end date of the current week (Sunday).
   *
   * @return the LocalDate representing the end of the week
   */
  public static LocalDate getEndOfWeek() {
    LocalDate startOfWeek = getStartOfWeek();
    LocalDate end = startOfWeek.plusDays(6);
    logger.debug("Calculated end of week: {}", end);
    return end;
  }

  /**
   * Validates if the provided email string matches a valid email format.
   *
   * @param email the email string to validate
   * @return true if the email is valid, false otherwise
   */
  public static boolean isEmail(String email) {
    logger.debug("Validating email: {}", email);
    boolean isValid = email != null && email.matches(EMAIL_REGEX);
    logger.debug("Email validation result: {}", isValid);
    return isValid;
  }

  /**
   * Validates if the provided password meets the required criteria (at least 8 characters,
   * containing at least one letter and one number).
   *
   * @param password the password string to validate
   * @return true if the password is valid, false otherwise
   */
  public static boolean isValidPassword(String password) {
    logger.debug("Validating password length: {}", password != null ? password.length() : null);
    boolean isValid = password != null && password.length() >= 8;
    logger.debug("Password validation result: {}", isValid);
    return isValid;
  }

  /**
   * Validates if the provided ID is positive (greater than 0).
   *
   * @param id the ID to validate
   * @param message the error message to include in the exception if invalid
   * @throws IllegalArgumentException if the ID is not positive
   */
  public static void validatePositiveId(long id, String message) {
    logger.debug("Validating ID: {}", id);
    if (id <= 0) {
      logger.error("Invalid ID: {} - {}", id, message);
      throw new IllegalArgumentException(message);
    }
    logger.debug("ID validation passed for: {}", id);
  }
}
