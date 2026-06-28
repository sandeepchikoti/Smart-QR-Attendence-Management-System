package com.university.attendance.common;

import java.util.Map;

public record ErrorResponse(
    boolean success,
    ErrorDetails error
) {
    public record ErrorDetails(
        String code,
        String message,
        Map<String, String> fields
    ) {}

    public static ErrorResponse error(String code, String message) {
        return new ErrorResponse(false, new ErrorDetails(code, message, null));
    }

    public static ErrorResponse error(String code, String message, Map<String, String> fields) {
        return new ErrorResponse(false, new ErrorDetails(code, message, fields));
    }
}
