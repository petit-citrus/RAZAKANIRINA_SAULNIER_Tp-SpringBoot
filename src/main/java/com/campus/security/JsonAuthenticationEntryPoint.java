package com.campus.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Clock;

public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorResponseWriter writer;

    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper, Clock clock) {
        this.writer = new SecurityErrorResponseWriter(objectMapper, clock);
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        writer.write(
                request,
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "NON_AUTHENTIFIE",
                "Une authentification est requise.");
    }
}
