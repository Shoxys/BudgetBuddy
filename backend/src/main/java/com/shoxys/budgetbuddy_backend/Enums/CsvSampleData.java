package com.shoxys.budgetbuddy_backend.Enums;

public enum CsvSampleData {
  VALID_SAMPLE("TestData/sample1.csv"),
  NULL_MERCHANT_SAMPLE("TestData/sample2.csv");

  private final String path;

  CsvSampleData(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
