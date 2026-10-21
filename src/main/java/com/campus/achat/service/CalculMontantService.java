package com.campus.achat.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculMontantService {

    private static final BigDecimal TAUX_REMISE_ACTIVE = new BigDecimal("0.10");
    private static final BigDecimal TAUX_REMISE_INACTIVE = BigDecimal.ZERO;
    private static final int DECIMALS = 2;

    public BigDecimal calculerMontant(BigDecimal prixUnitaire, int quantite, boolean carteActive) {
        if (prixUnitaire == null) {
            throw new IllegalArgumentException("Le prix unitaire est obligatoire.");
        }
        if (prixUnitaire.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être strictement positif.");
        }

        if (quantite < 1 || quantite > 6) {
            throw new IllegalArgumentException("La quantité doit être comprise entre 1 et 6.");
        }

        BigDecimal montantBrut = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        BigDecimal tauxRemise = carteActive ? TAUX_REMISE_ACTIVE : TAUX_REMISE_INACTIVE;

        BigDecimal montantRemise = montantBrut.multiply(tauxRemise);
        BigDecimal montantTotal = montantBrut.subtract(montantRemise);

        return montantTotal.setScale(DECIMALS, RoundingMode.HALF_UP);
    }
}