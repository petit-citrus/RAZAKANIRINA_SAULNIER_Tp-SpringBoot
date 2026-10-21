package com.campus.security;

import java.io.IOException;
import java.time.Clock;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorResponseWriter writer;

    public JsonAccessDeniedHandler(ObjectMapper objectMapper, Clock clock) {
        this.writer = new SecurityErrorResponseWriter(objectMapper, clock);
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        writer.write(
                request,
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "ACCES_REFUSE",
                "Vous n'avez pas les droits nécessaires.");
    }
}
