package com.campus.fidelite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;

import com.campus.client.Client;
import com.campus.client.ClientRepository;
import com.campus.common.error.BusinessConflictException;
import com.campus.common.error.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CarteFideliteServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-01-15T10:00:00Z"),
            ZoneOffset.UTC
    );

    @Mock
    private CarteFideliteRepository carteRepository;

    @Mock
    private ClientRepository clientRepository;

    private CarteFideliteService service;

    @BeforeEach
    void setUp() {
        service = new CarteFideliteService(
                carteRepository,
                clientRepository,
                FIXED_CLOCK
        );
    }

    @Test
    void creerOuRenouveler_clientSansCarte_creeCarteValableUnAn() {
        Long clientId = 10L;
        Client client = new Client("Alice", "alice@example.com", new com.campus.client.Adresse("1 rue Test", "Paris"));
        setId(client, clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(carteRepository.findByClientId(clientId)).thenReturn(Optional.empty());
        when(carteRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(CarteFidelite.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarteFidelite result = new CarteFideliteService(carteRepository, clientRepository, FIXED_CLOCK)
                .creerOuRenouveler(clientId);

        assertNotNull(result);
        assertEquals(client, result.getClient());
        assertEquals(LocalDateTime.of(2026, 1, 15, 10, 0, 0), result.getDateAchat());
        assertEquals(LocalDateTime.of(2027, 1, 15, 10, 0, 0), result.getDateExpiration());

        verify(clientRepository).findById(clientId);
        verify(carteRepository).findByClientId(clientId);
        verify(carteRepository).saveAndFlush(org.mockito.ArgumentMatchers.any(CarteFidelite.class));
    }

    @Test
    void creerOuRenouveler_clientAvecCarteActive_leveBusinessConflictExceptionSansEnregistrement() {
        Long clientId = 11L;
        Client client = new Client("Bob", "bob@example.com", new com.campus.client.Adresse("2 rue Test", "Lyon"));
        setId(client, clientId);

        CarteFidelite carteActive = new CarteFidelite(
                client,
                LocalDateTime.of(2026, 1, 1, 10, 0, 0),
                LocalDateTime.of(2027, 1, 1, 10, 0, 0)
        );

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(carteRepository.findByClientId(clientId)).thenReturn(Optional.of(carteActive));

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> service.creerOuRenouveler(clientId)
        );

        assertEquals("Le client possède déjà une carte de fidélité active.", exception.getMessage());

        verify(clientRepository).findById(clientId);
        verify(carteRepository).findByClientId(clientId);
        verify(carteRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any(CarteFidelite.class));
    }

    @Test
    void creerOuRenouveler_clientAvecCarteExpiree_renouvelleCarteAvecNouvellesDates() {
        Long clientId = 12L;
        Client client = new Client("Charlie", "charlie@example.com", new com.campus.client.Adresse("3 rue Test", "Marseille"));
        setId(client, clientId);

        CarteFidelite carteExpiree = new CarteFidelite(
                client,
                LocalDateTime.of(2025, 1, 15, 10, 0, 0),
                LocalDateTime.of(2025, 12, 31, 10, 0, 0)
        );

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(carteRepository.findByClientId(clientId)).thenReturn(Optional.of(carteExpiree));
        when(carteRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(CarteFidelite.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarteFidelite result = service.creerOuRenouveler(clientId);

        assertNotNull(result);
        assertEquals(LocalDateTime.of(2026, 1, 15, 10, 0, 0), result.getDateAchat());
        assertEquals(LocalDateTime.of(2027, 1, 15, 10, 0, 0), result.getDateExpiration());

        verify(clientRepository).findById(clientId);
        verify(carteRepository).findByClientId(clientId);
        verify(carteRepository).saveAndFlush(org.mockito.ArgumentMatchers.any(CarteFidelite.class));
    }

    private void setId(Client client, Long id) {
        try {
            Field field = Client.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(client, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Impossible de définir l'id du client.", e);
        }
    }
}