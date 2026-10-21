package com.campus.catalogue.concert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConcertResponse(
        Long id,
        String titre,
        Long artisteId,
        String artisteNom,
        Long salleId,
        String salleNom,
        String salleVille,
        LocalDateTime dateConcert,
        BigDecimal prix,
        Integer placesRestantes
) {
    public static ConcertResponse from(Concert concert, long billetsVendus) {
        int placesRestantes = Math.toIntExact(
                (long) concert.getSalle().getCapacite() - billetsVendus);
        return new ConcertResponse(
                concert.getId(),
                concert.getTitre(),
                concert.getArtiste().getId(),
                concert.getArtiste().getNom(),
                concert.getSalle().getId(),
                concert.getSalle().getNom(),
                concert.getSalle().getVille(),
                concert.getDateConcert(),
                concert.getPrix(),
                placesRestantes);
    }
}
