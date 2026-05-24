package com.sliit.vehiclerentalsystem01.core.exceptions;

import java.time.Instant;
import java.util.List;

/**
 * Structured error response for REST endpoints and error pages.
 */
public record ApiError(
        String path,
        int status,
        String message,
        List<FieldError> fields,
        long timestamp
) {
    public ApiError(String path, int status, String message) {
        this(path, status, message, List.of(), Instant.now().toEpochMilli());
    }

    public ApiError(String path, int status, String message, List<FieldError> fields) {
        this(path, status, message, fields, Instant.now().toEpochMilli());
    }

    public record FieldError(String field, String message) {}
}
