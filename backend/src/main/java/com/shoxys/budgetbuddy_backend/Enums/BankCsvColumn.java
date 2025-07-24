package com.shoxys.budgetbuddy_backend.Enums;

/** Defines column names for parsing bank CSV files. */
public enum BankCsvColumn {
  DATE("Date"),
  AMOUNT("Amount"),
  ACCOUNT_NUMBER("Account Number"),
  UNUSED("Empty"),
  TRANSACTION_TYPE("Transaction Type"),
  DETAILS("Transaction Details"),
  BALANCE("Balance"),
  CATEGORY("Category"),
  MERCHANT("Merchant Name");

  private final String title;

  BankCsvColumn(String title) {
    this.title = title;
  }

  /**
   * Returns the string representation of the column name.
   *
   * @return the column title
   */
  public String getStr() {
    return title;
  }
}
