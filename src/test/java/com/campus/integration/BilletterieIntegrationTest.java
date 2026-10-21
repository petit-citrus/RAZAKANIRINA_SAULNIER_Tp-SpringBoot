package com.campus.integration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.campus.catalogue.artiste.Artiste;
import com.campus.catalogue.artiste.ArtisteRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class BilletterieIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.4");

    @Autowired
    private ArtisteRepository artisteRepository;

    @Test
    void contexteDemarreEtContrainteNomArtisteEstRespectee() {
        assertThatThrownBy(() -> artisteRepository.saveAndFlush(
                new Artiste("Les Astres", "Jazz")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
