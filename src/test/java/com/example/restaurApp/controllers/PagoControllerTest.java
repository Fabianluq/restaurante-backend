package com.example.restaurApp.controllers;

import com.example.restaurApp.config.TestSecurityConfig;
import com.example.restaurApp.dto.PagoRequest;
import com.example.restaurApp.dto.PagoResponse;
import com.example.restaurApp.service.PagoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = PagoController.class)
@Import(TestSecurityConfig.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;

    @Test
    @WithMockUser(roles = {"MESERO"})
    void procesarPago_debePermitirMesero() throws Exception {
        Mockito.when(pagoService.procesarPago(Mockito.any(PagoRequest.class), Mockito.anyString()))
                .thenReturn(new PagoResponse());

        String body = "{\n" +
                "  \"pedidoId\": 1,\n" +
                "  \"monto\": 20.00,\n" +
                "  \"metodoPago\": \"efectivo\"\n" +
                "}";

        mockMvc.perform(post("/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .with(csrf())
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void listarPagos_anonimoDebeFallar() throws Exception {
        mockMvc.perform(get("/pagos"))
                .andExpect(status().isUnauthorized());
    }
}


