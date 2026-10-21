package com.campus.fidelite;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.client.Client;
import com.campus.client.ClientRepository;
import com.campus.common.error.BusinessConflictException;
import com.campus.common.error.ResourceNotFoundException;

@Service
public class CarteFideliteService {

    private final CarteFideliteRepository carteRepository;
    private final ClientRepository clientRepository;
    private final Clock clock;

    public CarteFideliteService(
            CarteFideliteRepository carteRepository,
            ClientRepository clientRepository,
            Clock clock) {
        this.carteRepository = carteRepository;
        this.clientRepository = clientRepository;
        this.clock = clock;
    }

    @Transactional
    public CarteFidelite creerOuRenouveler(Long clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client introuvable."));

        LocalDateTime maintenant = LocalDateTime.now(clock);

        CarteFidelite carte = carteRepository.findByClientId(clientId)
                .orElse(null);

        if (carte != null) {
            if (carte.getDateExpiration().isAfter(maintenant)) {
                throw new BusinessConflictException(
                        "Le client possède déjà une carte de fidélité active.");
            }

            carte.setDateAchat(maintenant);
            carte.setDateExpiration(maintenant.plusYears(1));
        } else {
            carte = new CarteFidelite(
                    client,
                    maintenant,
                    maintenant.plusYears(1)
            );
        }

        return carteRepository.saveAndFlush(carte);
    }
}