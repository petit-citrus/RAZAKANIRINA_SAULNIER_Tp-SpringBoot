package com.campus.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.campus.client.ClientController;
import com.campus.client.ClientService;
import com.campus.client.dto.ClientRegistrationRequest;
import com.campus.client.dto.ClientResponse;
import com.campus.common.error.GlobalExceptionHandler;
import com.campus.config.FixedClockTestConfiguration;
import com.campus.security.SecurityConfig;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClientController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class,
        FixedClockTestConfiguration.class,
        WebMvcTestConfiguration.class
})
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    @Test
    void inscriptionRetourne201() throws Exception {
        ClientRegistrationRequest request =
                new ClientRegistrationRequest("Alice", "alice@test.fr", "Rue A", "Paris");
        when(clientService.enregistrer(any())).thenReturn(
                new ClientResponse(10L, "Alice", "alice@test.fr", "Rue A", "Paris"));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/clients/10"));
    }
}
