package com.example.restaurApp.controllers;

import com.example.restaurApp.config.TestSecurityConfig;
import com.example.restaurApp.dto.ReporteVentasRequest;
import com.example.restaurApp.dto.ReporteVentasResponse;
import com.example.restaurApp.service.ReporteService;
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

@WebMvcTest(controllers = ReporteController.class)
@Import(TestSecurityConfig.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void generarReporteVentas_adminPermitido() throws Exception {
        Mockito.when(reporteService.generarReporteVentas(Mockito.any(ReporteVentasRequest.class), Mockito.anyString()))
                .thenReturn(new ReporteVentasResponse());

        String body = "{\n" +
                "  \"tipoReporte\": \"DIARIO\"\n" +
                "}";

        mockMvc.perform(post("/reportes/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .with(csrf())
                        .content(body))
                .andExpect(status().isOk());
    }
}


