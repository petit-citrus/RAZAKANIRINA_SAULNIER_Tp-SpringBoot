package com.campus.client.dto;

public record ClientResponse(
        Long id,
        String nom,
        String email,
        String rue,
        String ville
) {}