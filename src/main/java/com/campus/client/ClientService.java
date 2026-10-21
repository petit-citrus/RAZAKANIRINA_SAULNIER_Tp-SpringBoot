package com.campus.client;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.client.dto.ClientRegistrationRequest;
import com.campus.client.dto.ClientResponse;
import com.campus.common.error.BusinessConflictException;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientResponse enregistrer(ClientRegistrationRequest request) {
        String emailNormalise =
                request.email().trim().toLowerCase(Locale.ROOT);

        if (clientRepository.findByEmail(emailNormalise).isPresent()) {
            throw new BusinessConflictException("L'email est déjà utilisé.");
        }

        Adresse adresse = new Adresse(request.rue(), request.ville());
        Client client = new Client(request.nom(), emailNormalise, adresse);

        Client clientEnregistre = clientRepository.saveAndFlush(client);

        return new ClientResponse(
                clientEnregistre.getId(),
                clientEnregistre.getNom(),
                clientEnregistre.getEmail(),
                clientEnregistre.getAdresse().getRue(),
                clientEnregistre.getAdresse().getVille()
        );
    }
}