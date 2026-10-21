package com.campus.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campus.client.dto.ClientRegistrationRequest;
import com.campus.client.dto.ClientResponse;
import com.campus.common.error.BusinessConflictException;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void enregistrer_inscriptionValide_enregistreClientEtRetourneClientResponse() {
        ClientRegistrationRequest request = new ClientRegistrationRequest(
                "Alice Martin",
                "alice@example.com",
                "12 rue des Fleurs",
                "Paris"
        );

        Client clientEnregistre = new Client("Alice Martin", "alice@example.com", new Adresse("12 rue des Fleurs", "Paris"));
        setId(clientEnregistre, 42L);

        when(clientRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(clientRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Client.class)))
                .thenReturn(clientEnregistre);

        ClientResponse response = clientService.enregistrer(request);

        assertEquals(42L, response.id());
        assertEquals("Alice Martin", response.nom());
        assertEquals("alice@example.com", response.email());
        assertEquals("12 rue des Fleurs", response.rue());
        assertEquals("Paris", response.ville());

        verify(clientRepository).findByEmail("alice@example.com");
        verify(clientRepository).saveAndFlush(org.mockito.ArgumentMatchers.any(Client.class));
    }

    @Test
    void enregistrer_emailAvecEspacesEtMajuscules_normaliseEmailEtEnregistre() {
        ClientRegistrationRequest request = new ClientRegistrationRequest(
                "Bob Dupont",
                "  BOB@EXAMPLE.COM  ",
                "8 avenue du Midi",
                "Lyon"
        );

        Client clientEnregistre = new Client("Bob Dupont", "bob@example.com", new Adresse("8 avenue du Midi", "Lyon"));
        setId(clientEnregistre, 99L);

        when(clientRepository.findByEmail("bob@example.com")).thenReturn(Optional.empty());
        when(clientRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Client.class)))
                .thenReturn(clientEnregistre);

        ClientResponse response = clientService.enregistrer(request);

        assertEquals("bob@example.com", response.email());

        verify(clientRepository).findByEmail("bob@example.com");
        verify(clientRepository).saveAndFlush(org.mockito.ArgumentMatchers.any(Client.class));
    }

    @Test
    void enregistrer_emailDejaExistant_leveBusinessConflictExceptionEtNeSauvePas() {
        ClientRegistrationRequest request = new ClientRegistrationRequest(
                "Camille Leroy",
                "camille@example.com",
                "15 boulevard Nord",
                "Marseille"
        );

        Client clientExistant = new Client("Camille Leroy", "camille@example.com", new Adresse("15 boulevard Nord", "Marseille"));
        setId(clientExistant, 77L);

        when(clientRepository.findByEmail("camille@example.com")).thenReturn(Optional.of(clientExistant));

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> clientService.enregistrer(request)
        );

        assertEquals("L'email est déjà utilisé.", exception.getMessage());

        verify(clientRepository).findByEmail("camille@example.com");
        verify(clientRepository, org.mockito.Mockito.never())
                .saveAndFlush(org.mockito.ArgumentMatchers.any(Client.class));
    }

    private void setId(Client client, Long id) {
        try {
            Field field = Client.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(client, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Impossible de définir l'id du client pour le test.", e);
        }
    }
}