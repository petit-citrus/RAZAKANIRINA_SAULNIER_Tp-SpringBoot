package com.campus.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration(proxyBeanMethods = false)
public class FixedClockTestConfiguration {

    @Bean
    @Primary
    Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-01-15T12:00:00Z"),
                ZoneOffset.UTC);
    }
}
