package com.campus.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.campus.catalogue.artiste.ArtisteController;
import com.campus.catalogue.artiste.ArtisteRequest;
import com.campus.catalogue.artiste.ArtisteResponse;
import com.campus.catalogue.artiste.ArtisteService;
import com.campus.catalogue.concert.ConcertController;
import com.campus.catalogue.concert.ConcertService;
import com.campus.catalogue.salle.SalleController;
import com.campus.catalogue.salle.SalleRequest;
import com.campus.catalogue.salle.SalleResponse;
import com.campus.catalogue.salle.SalleService;
import com.campus.common.error.BusinessConflictException;
import com.campus.common.error.GlobalExceptionHandler;
import com.campus.common.error.ResourceNotFoundException;
import com.campus.config.FixedClockTestConfiguration;
import com.campus.security.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
        ArtisteController.class,
        SalleController.class,
        ConcertController.class
})
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class,
        FixedClockTestConfiguration.class,
        WebMvcTestConfiguration.class
})
class CatalogueControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtisteService artisteService;

    @MockitoBean
    private SalleService salleService;

    @MockitoBean
    private ConcertService concertService;

    @Test
    void anonymeNePeutPasCreerUneSalle() throws Exception {
        mockMvc.perform(post("/api/admin/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"Salle test","ville":"Paris","capacite":100}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").value("2026-01-15T12:00Z"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").value("/api/admin/salles"));
    }

    @Test
    @WithMockUser(username = "client1@campus.fr", roles = "CLIENT")
    void clientNePeutPasCreerUneSalle() throws Exception {
        mockMvc.perform(post("/api/admin/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"Salle test","ville":"Paris","capacite":100}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").value("2026-01-15T12:00Z"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("ACCES_REFUSE"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").value("/api/admin/salles"));
    }

    @Test
    @WithMockUser(username = "producteur", roles = "PRODUCTEUR")
    void artisteInvalideRetourne400() throws Exception {
        mockMvc.perform(post("/api/production/artistes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"","genreMusical":"Rock"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ECHOUEE"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith("nom: ")))
                .andExpect(jsonPath("$.path").value("/api/production/artistes"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void doublonDeSalleRetourne409() throws Exception {
        when(salleService.creer(any(SalleRequest.class)))
                .thenThrow(new BusinessConflictException("Salle déjà existante."));

        mockMvc.perform(post("/api/admin/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nom":"Le Zenith","ville":"Paris","capacite":5000}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("CONFLIT_METIER"))
                .andExpect(jsonPath("$.message").value("Salle déjà existante."))
                .andExpect(jsonPath("$.path").value("/api/admin/salles"));
    }

    @Test
    void concertInexistantRetourne404() throws Exception {
        when(concertService.trouverParId(999L))
                .thenThrow(new ResourceNotFoundException("Concert introuvable : 999"));

        mockMvc.perform(get("/api/concerts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESSOURCE_INTROUVABLE"))
                .andExpect(jsonPath("$.message").value("Concert introuvable : 999"))
                .andExpect(jsonPath("$.path").value("/api/concerts/999"));
    }
}
