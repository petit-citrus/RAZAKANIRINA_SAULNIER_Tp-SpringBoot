package com.campus.common.error;

public record ErrorResponse(
        String timestamp,
        int status,
        String code,
        String message,
        String path
) {
}
