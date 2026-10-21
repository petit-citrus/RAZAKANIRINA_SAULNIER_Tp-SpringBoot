package com.campus.achat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AchatRequest(
        @NotNull(message = "L'identifiant du concert est obligatoire.")
        @Min(value = 1, message = "L'identifiant du concert doit être strictement positif.")
        Long concertId,

        @NotNull(message = "L'identifiant du client est obligatoire.")
        @Min(value = 1, message = "L'identifiant du client doit être strictement positif.")
        Long clientId,

        @NotNull(message = "La quantité est obligatoire.")
        @Min(value = 1, message = "La quantité doit être comprise entre 1 et 6.")
        @Max(value = 6, message = "La quantité doit être comprise entre 1 et 6.")
        Integer quantite
) {
}