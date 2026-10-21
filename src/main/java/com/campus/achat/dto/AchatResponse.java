package com.campus.achat.dto;

import java.math.BigDecimal;

public record AchatResponse(
        String code,
        Integer quantite,
        BigDecimal prixUnitaire,
        BigDecimal tauxRemise,
        BigDecimal montantTotal,
        Integer placesRestantes
) {
}