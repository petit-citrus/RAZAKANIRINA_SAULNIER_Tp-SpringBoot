package com.campus.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.campus.achat.AchatBillets;
import com.campus.achat.AchatBilletsRepository;
import com.campus.achat.dto.AchatRequest;
import com.campus.achat.dto.AchatResponse;
import com.campus.achat.service.AchatService;
import com.campus.catalogue.artiste.Artiste;
import com.campus.catalogue.artiste.ArtisteRepository;
import com.campus.catalogue.concert.Concert;
import com.campus.catalogue.concert.ConcertRepository;
import com.campus.catalogue.salle.Salle;
import com.campus.catalogue.salle.SalleRepository;
import com.campus.client.Client;
import com.campus.client.ClientRepository;
import com.campus.common.error.BusinessConflictException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class AchatConcurrenceIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.4");

    @Autowired
    private AchatService achatService;

    @Autowired
    private AchatBilletsRepository achatBilletsRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ArtisteRepository artisteRepository;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private ClientRepository clientRepository;

    private ExecutorService executor;
    private Long concertId;
    private Long clientId;
    private Authentication authentication;

    @BeforeEach
    void preparerConcertAvecCinqPlaces() {
        Artiste artiste = artisteRepository.findById(1L).orElseThrow();
        Salle salle = salleRepository.saveAndFlush(
                new Salle("Salle concurrence", "Paris", 50));
        Concert concert = concertRepository.saveAndFlush(
                new Concert(
                        "Concert concurrence",
                        artiste,
                        salle,
                        LocalDateTime.of(2027, 8, 20, 20, 0),
                        new BigDecimal("25.00")));

        Client client = clientRepository.findById(1L).orElseThrow();
        for (int i = 1; i <= 8; i++) {
            int quantite = i <= 7 ? 6 : 3;
            achatBilletsRepository.save(new AchatBillets(
                    "CONCURRENCE-" + i,
                    concert,
                    client,
                    quantite,
                    new BigDecimal(quantite * 25 + ".00"),
                    LocalDateTime.of(2026, 10, 21, 10, 0)));
        }
        achatBilletsRepository.flush();

        concertId = concert.getId();
        clientId = client.getId();
        authentication = UsernamePasswordAuthenticationToken.authenticated(
                client.getEmail(), "n/a", java.util.List.of());
        executor = Executors.newFixedThreadPool(2);
    }

    @AfterEach
    void arreterExecutors() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Test
    void uneSeuleTransactionAcheteQuatreBilletsSousConcurrence()
            throws Exception {
        CountDownLatch depart = new CountDownLatch(1);

        Future<AchatResponse> premier = executor.submit(() -> {
            depart.await();
            return achatService.creer(
                    new AchatRequest(concertId, clientId, 4),
                    authentication);
        });
        Future<AchatResponse> second = executor.submit(() -> {
            depart.await();
            return achatService.creer(
                    new AchatRequest(concertId, clientId, 4),
                    authentication);
        });

        depart.countDown();

        int succes = 0;
        int conflits = 0;
        for (Future<AchatResponse> achat :
                java.util.List.of(premier, second)) {
            try {
                assertThat(achat.get(30, TimeUnit.SECONDS)).isNotNull();
                succes++;
            } catch (ExecutionException exception) {
                assertThat(exception.getCause())
                        .isInstanceOf(BusinessConflictException.class);
                conflits++;
            }
        }

        long stockFinal = achatBilletsRepository
                .sommeQuantiteVendueParConcert(concertId);

        assertThat(succes).isEqualTo(1);
        assertThat(conflits).isEqualTo(1);
        assertThat(stockFinal).isEqualTo(49L);
    }
}
