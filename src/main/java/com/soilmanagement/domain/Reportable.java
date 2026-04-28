package com.soilmanagement.domain;

public interface Reportable {
    String generateReport();
    String getSummary();
    boolean isReportDue();
}
