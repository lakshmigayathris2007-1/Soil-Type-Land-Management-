package com.soilmanagement.domain;

public interface Analyzable {
    double calculateIndex();
    String analyzeCondition();
    boolean meetsStandard(String standardCode);
}
