package com.campus.catalogue.artiste;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtisteRequest(
        @NotBlank @Size(max = 255) String nom,
        @Size(max = 255) String genreMusical
) {
}
