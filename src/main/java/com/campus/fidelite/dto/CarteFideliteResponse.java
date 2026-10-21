package com.campus.fidelite.dto;

import java.time.LocalDateTime;

public record CarteFideliteResponse(
        Long id,
        Long clientId,
        LocalDateTime acheteeLe,
        LocalDateTime expireLe
) {
}