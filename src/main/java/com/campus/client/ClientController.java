package com.campus.client;

import java.net.URI;

import com.campus.client.dto.ClientRegistrationRequest;
import com.campus.client.dto.ClientResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> enregistrer(
            @Valid @RequestBody ClientRegistrationRequest request) {

        ClientResponse response = clientService.enregistrer(request);

        return ResponseEntity
                .created(URI.create("/api/clients/" + response.id()))
                .body(response);
    }
}