package com.soilmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler
 * Centralised exception handling for all REST controllers.
 * Converts exceptions to structured JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSoilDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSoilData(InvalidSoilDataException ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.BAD_REQUEST.value(), "INVALID_SOIL_DATA", ex.getMessage());
        body.put("field", ex.getFieldName());
        body.put("rejectedValue", ex.getRejectedValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(LandNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLandNotFound(LandNotFoundException ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.NOT_FOUND.value(), "LAND_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.BAD_REQUEST.value(), "ILLEGAL_ARGUMENT", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointer(NullPointerException ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "NULL_POINTER", "A required value was null");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> body = buildErrorBody(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> buildErrorBody(int status, String errorCode, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status);
        body.put("errorCode", errorCode);
        body.put("message",   message);
        return body;
    }
}
