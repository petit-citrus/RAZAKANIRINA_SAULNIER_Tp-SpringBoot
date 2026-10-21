package com.campus.catalogue.artiste;

public record ArtisteResponse(
        Long id,
        String nom,
        String genreMusical
) {
    public static ArtisteResponse from(Artiste artiste) {
        return new ArtisteResponse(artiste.getId(), artiste.getNom(), artiste.getGenreMusical());
    }
}
