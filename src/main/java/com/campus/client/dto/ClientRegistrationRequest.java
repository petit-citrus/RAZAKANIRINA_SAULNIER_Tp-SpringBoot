package com.campus.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRegistrationRequest(

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 150)
        String nom,

        @NotBlank(message = "L'email est obligatoire.")
        @Email(message = "L'email doit avoir un format valide.")
        @Size(max = 255)
        String email,

        @NotBlank(message = "La rue est obligatoire.")
        @Size(max = 255)
        String rue,

        @NotBlank(message = "La ville est obligatoire.")
        @Size(max = 150)
        String ville
) {}