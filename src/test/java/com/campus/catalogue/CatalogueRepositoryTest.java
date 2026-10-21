package com.campus.catalogue;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import com.campus.catalogue.concert.Concert;
import com.campus.catalogue.concert.ConcertRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class CatalogueRepositoryTest extends RepositoryMySqlTestConfiguration {

    @Autowired
    private ConcertRepository concertRepository;

    @Test
    void rechercheFiltreParDateEtArtiste() {
        LocalDateTime du = LocalDateTime.of(2027, 4, 1, 0, 0);
        LocalDateTime au = LocalDateTime.of(2027, 4, 30, 23, 59);

        List<Concert> concerts = concertRepository.rechercher(du, au, 2L);

        assertThat(concerts)
                .extracting(Concert::getId)
                .containsExactly(2L);
    }

    @Test
    void rechercheSansFiltresRetourneTousLesConcerts() {
        assertThat(concertRepository.rechercher(null, null, null))
                .hasSize(3)
                .extracting(Concert::getId)
                .containsExactly(1L, 2L, 3L);
    }
}
