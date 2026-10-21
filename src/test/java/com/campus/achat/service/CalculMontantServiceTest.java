package com.campus.achat.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CalculMontantServiceTest {

    private final CalculMontantService service = new CalculMontantService();

    @Test
    void calculerMontant_quantite1AcceptéeSansCarte_retourne50_00() {
        BigDecimal result = service.calculerMontant(new BigDecimal("50.00"), 1, false);

        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    void calculerMontant_quantite6AcceptéeSansCarte_retourne300_00() {
        BigDecimal result = service.calculerMontant(new BigDecimal("50.00"), 6, false);

        assertEquals(new BigDecimal("300.00"), result);
    }

    @Test
    void calculerMontant_quantite0Refusée_lanceException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerMontant(new BigDecimal("50.00"), 0, false)
        );

        assertTrue(exception.getMessage().contains("comprise entre 1 et 6"));
    }

    @Test
    void calculerMontant_quantite7Refusée_lanceException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerMontant(new BigDecimal("50.00"), 7, false)
        );

        assertTrue(exception.getMessage().contains("comprise entre 1 et 6"));
    }

    @Test
    void calculerMontant_prixNullRefusé_lanceException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerMontant(null, 1, false)
        );

        assertTrue(exception.getMessage().contains("obligatoire"));
    }

    @Test
    void calculerMontant_prixZeroRefusé_lanceException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerMontant(BigDecimal.ZERO, 1, false)
        );

        assertTrue(exception.getMessage().contains("strictement positif"));
    }

    @Test
    void calculerMontant_prixNegatifRefusé_lanceException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculerMontant(new BigDecimal("-10.00"), 1, false)
        );

        assertTrue(exception.getMessage().contains("strictement positif"));
    }

    @Test
    void calculerMontant_avecCarteActive19_99X1_retourne17_99() {
        BigDecimal result = service.calculerMontant(new BigDecimal("19.99"), 1, true);

        assertEquals(new BigDecimal("17.99"), result);
    }

    @Test
    void calculerMontant_avecCarteActive50_00X3_retourne135_00() {
        BigDecimal result = service.calculerMontant(new BigDecimal("50.00"), 3, true);

        assertEquals(new BigDecimal("135.00"), result);
    }

    @Test
    void calculerMontant_sansCarte50_00X1_retourne50_00() {
        BigDecimal result = service.calculerMontant(new BigDecimal("50.00"), 1, false);

        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    void calculerMontant_avecCarteActive50_00X1_retourne45_00() {
        BigDecimal result = service.calculerMontant(new BigDecimal("50.00"), 1, true);

        assertEquals(new BigDecimal("45.00"), result);
    }

    @Test
    void calculerMontant_avecCarteInactive50_00X1_retourne50_00() {
        BigDecimal result = service.calculerMontant(new BigDecimal("50.00"), 1, false);

        assertEquals(new BigDecimal("50.00"), result);
    }
}