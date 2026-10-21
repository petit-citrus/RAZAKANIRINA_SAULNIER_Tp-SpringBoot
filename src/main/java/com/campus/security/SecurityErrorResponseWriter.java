package com.campus.security;

import java.io.IOException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.campus.common.error.ErrorResponse;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class SecurityErrorResponseWriter {

    private final ObjectMapper objectMapper;
    private final Clock clock;

    SecurityErrorResponseWriter(ObjectMapper objectMapper, Clock clock) {
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    void write(HttpServletRequest request,
               HttpServletResponse response,
               int status,
               String code,
               String message) throws IOException {
        ErrorResponse error = new ErrorResponse(
                OffsetDateTime.now(clock.withZone(ZoneOffset.UTC)).toString(),
                status,
                code,
                message,
                request.getRequestURI());

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
