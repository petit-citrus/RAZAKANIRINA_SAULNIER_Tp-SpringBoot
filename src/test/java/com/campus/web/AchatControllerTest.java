package com.campus.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import com.campus.achat.AchatController;
import com.campus.achat.dto.AchatRequest;
import com.campus.achat.dto.AchatResponse;
import com.campus.achat.service.AchatService;
import com.campus.common.error.GlobalExceptionHandler;
import com.campus.config.FixedClockTestConfiguration;
import com.campus.security.SecurityConfig;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AchatController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class,
        FixedClockTestConfiguration.class,
        WebMvcTestConfiguration.class
})
class AchatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AchatService achatService;

    @Test
    void anonymeEstRefuseAvec401() throws Exception {
        mockMvc.perform(post("/api/achats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"concertId":1,"clientId":1,"quantite":1}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
    }

    @Test
    @WithMockUser(username = "producteur", roles = "PRODUCTEUR")
    void roleInsuffisantEstRefuseAvec403() throws Exception {
        mockMvc.perform(post("/api/achats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"concertId":1,"clientId":1,"quantite":1}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
    }

    @Test
    @WithMockUser(username = "client1@campus.fr", roles = "CLIENT")
    void clientPeutCreerUnAchat() throws Exception {
        AchatRequest request = new AchatRequest(1L, 1L, 2);
        when(achatService.creer(any(), any())).thenReturn(
                new AchatResponse("ACHAT-1", 2, new BigDecimal("45.00"),
                        BigDecimal.ZERO, new BigDecimal("90.00"), 3));

        mockMvc.perform(post("/api/achats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ACHAT-1"));
    }

    @Test
    @WithMockUser(username = "client1@campus.fr", roles = "CLIENT")
    void erreurMetierDeRequeteEstRetourneeEn400() throws Exception {
        when(achatService.creer(any(), any()))
                .thenThrow(new IllegalArgumentException("Requête d'achat invalide."));

        mockMvc.perform(post("/api/achats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"concertId":1,"clientId":1,"quantite":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));
    }
}
