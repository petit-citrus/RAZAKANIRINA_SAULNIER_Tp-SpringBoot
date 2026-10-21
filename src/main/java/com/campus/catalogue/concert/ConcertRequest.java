package com.campus.catalogue.concert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ConcertRequest(
        @NotBlank @Size(max = 255) String titre,
        @NotNull Long artisteId,
        @NotNull Long salleId,
        @NotNull @Future LocalDateTime dateConcert,
        @NotNull @Positive BigDecimal prix
) {
}
