package com.campus.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import com.campus.client.Client;
import com.campus.client.ClientRepository;
import com.campus.client.Adresse;
import com.campus.fidelite.CarteFidelite;
import com.campus.fidelite.CarteFideliteController;
import com.campus.fidelite.CarteFideliteService;
import com.campus.common.error.GlobalExceptionHandler;
import com.campus.config.FixedClockTestConfiguration;
import com.campus.security.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CarteFideliteController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class,
        FixedClockTestConfiguration.class,
        WebMvcTestConfiguration.class
})
class CarteFideliteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarteFideliteService carteService;

    @MockitoBean
    private ClientRepository clientRepository;

    @Test
    void anonymeEstRefuseAvec401() throws Exception {
        mockMvc.perform(post("/api/clients/1/carte-fidelite"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
    }

    @Test
    @WithMockUser(username = "autre@test.fr", roles = "CLIENT")
    void clientNePeutPasCreerLaCarteDUnAutreClient() throws Exception {
        Client client = new Client("Alice", "alice@test.fr",
                new Adresse("Rue A", "Paris"));
        when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(client));

        mockMvc.perform(post("/api/clients/1/carte-fidelite"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
    }

    @Test
    @WithMockUser(username = "alice@test.fr", roles = "CLIENT")
    void clientPeutCreerSaCarte() throws Exception {
        Client client = new Client("Alice", "alice@test.fr",
                new Adresse("Rue A", "Paris"));
        when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(client));
        CarteFidelite carte = new CarteFidelite(
                client,
                LocalDateTime.of(2026, 1, 15, 12, 0),
                LocalDateTime.of(2027, 1, 15, 12, 0));
        when(carteService.creerOuRenouveler(1L)).thenReturn(carte);

        mockMvc.perform(post("/api/clients/1/carte-fidelite"))
                .andExpect(status().isCreated());
    }
}
