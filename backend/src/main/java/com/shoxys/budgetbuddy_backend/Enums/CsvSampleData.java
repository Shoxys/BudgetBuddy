package com.shoxys.budgetbuddy_backend.Enums;

/** Defines file paths for sample CSV data used in testing. */
public enum CsvSampleData {
  VALID_SAMPLE("TestData/sample1.csv"),
  NULL_MERCHANT_SAMPLE("TestData/sample2.csv");

  private final String path;

  CsvSampleData(String path) {
    this.path = path;
  }

  /**
   * Returns the file path of the sample CSV data.
   *
   * @return the file path
   */
  public String getPath() {
    return path;
  }
}
