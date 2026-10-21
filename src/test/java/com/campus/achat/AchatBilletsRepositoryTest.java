package com.campus.achat;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.campus.catalogue.concert.Concert;
import com.campus.catalogue.concert.ConcertRepository;
import com.campus.client.Client;
import com.campus.client.ClientRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import com.campus.catalogue.RepositoryMySqlTestConfiguration;

@DataJpaTest
class AchatBilletsRepositoryTest extends RepositoryMySqlTestConfiguration {

    @Autowired
    private AchatBilletsRepository achatBilletsRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void sommeQuantiteVendueParConcertRetourneLaSomme() {
        Concert concert = concertRepository.findById(1L).orElseThrow();
        Client client = clientRepository.findById(1L).orElseThrow();

        achatBilletsRepository.saveAndFlush(new AchatBillets(
                "TEST-AGGREGATION-1",
                concert,
                client,
                2,
                new BigDecimal("90.00"),
                LocalDateTime.of(2026, 1, 15, 12, 0)));
        achatBilletsRepository.saveAndFlush(new AchatBillets(
                "TEST-AGGREGATION-2",
                concert,
                client,
                3,
                new BigDecimal("135.00"),
                LocalDateTime.of(2026, 1, 15, 12, 0)));

        assertThat(achatBilletsRepository.sommeQuantiteVendueParConcert(1L))
                .isEqualTo(5L);
    }
}
