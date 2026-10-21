package com.campus.fidelite;

import java.net.URI;

import com.campus.fidelite.dto.CarteFideliteResponse;
import com.campus.common.error.BusinessConflictException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.client.Client;
import com.campus.client.ClientRepository;
import com.campus.common.error.ResourceNotFoundException;

@RestController
@RequestMapping("/api/clients/{clientId}/carte-fidelite")
public class CarteFideliteController {

    private final CarteFideliteService carteService;
    private final ClientRepository clientRepository;

    public CarteFideliteController(
            CarteFideliteService carteService,
            ClientRepository clientRepository) {

        this.carteService = carteService;
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<CarteFideliteResponse> creer(
            @PathVariable Long clientId,
            Authentication authentication) {

        if (authentication == null) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Accès refusé.");
        }

        boolean estAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!estAdmin) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Client introuvable."));

            if (!client.getEmail().equalsIgnoreCase(authentication.getName())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Vous ne pouvez pas créer une carte pour un autre client.");
            }
        }

        CarteFidelite carte = carteService.creerOuRenouveler(clientId);

        CarteFideliteResponse response = new CarteFideliteResponse(
                carte.getId(),
                carte.getClient().getId(),
                carte.getDateAchat(),
                carte.getDateExpiration()
        );

        return ResponseEntity
                .created(URI.create("/api/clients/" + clientId
                        + "/carte-fidelite"))
                .body(response);
    }
}