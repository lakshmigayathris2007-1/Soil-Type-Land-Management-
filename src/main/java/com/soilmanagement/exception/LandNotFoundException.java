package com.soilmanagement.exception;

/**
 * Custom Exception: LandNotFoundException
 * Thrown when a land record is not found by its survey number or ID.
 */
public class LandNotFoundException extends RuntimeException {

    private final Long landId;
    private final String surveyNumber;

    public LandNotFoundException(Long landId) {
        super("Land record not found with ID: " + landId);
        this.landId = landId;
        this.surveyNumber = null;
    }

    public LandNotFoundException(String surveyNumber) {
        super("Land record not found with survey number: " + surveyNumber);
        this.landId = null;
        this.surveyNumber = surveyNumber;
    }

    public LandNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.landId = null;
        this.surveyNumber = null;
    }

    public Long getLandId()        { return landId; }
    public String getSurveyNumber(){ return surveyNumber; }
}
