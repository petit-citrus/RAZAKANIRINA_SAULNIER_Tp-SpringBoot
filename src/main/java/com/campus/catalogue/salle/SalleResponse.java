package com.campus.catalogue.salle;

public record SalleResponse(
        Long id,
        String nom,
        String ville,
        Integer capacite
) {
    public static SalleResponse from(Salle salle) {
        return new SalleResponse(salle.getId(), salle.getNom(), salle.getVille(), salle.getCapacite());
    }
}
