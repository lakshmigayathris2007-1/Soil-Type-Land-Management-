package com.soilmanagement.exception;

/**
 * Custom Exception: InvalidSoilDataException
 * Thrown when soil sample data fails validation checks (e.g., pH out of range).
 * Extends RuntimeException — unchecked exception.
 */
public class InvalidSoilDataException extends RuntimeException {

    private final String fieldName;
    private final Object rejectedValue;

    public InvalidSoilDataException(String message) {
        super(message);
        this.fieldName = "unknown";
        this.rejectedValue = null;
    }

    public InvalidSoilDataException(String fieldName, Object rejectedValue, String message) {
        super(message);
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
    }

    public InvalidSoilDataException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = "unknown";
        this.rejectedValue = null;
    }

    public String getFieldName()    { return fieldName; }
    public Object getRejectedValue(){ return rejectedValue; }

    @Override
    public String toString() {
        return "InvalidSoilDataException{field='" + fieldName
               + "', rejectedValue=" + rejectedValue
               + ", message='" + getMessage() + "'}";
    }
}
