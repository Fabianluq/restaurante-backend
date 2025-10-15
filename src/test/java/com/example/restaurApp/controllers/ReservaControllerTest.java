package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ReservaRequest;
import com.example.restaurApp.dto.ReservaResponse;
import com.example.restaurApp.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservaController.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @Test
    @WithMockUser(roles = {"MESERO"})
    void crearReserva_meseroPermitido() throws Exception {
        Mockito.when(reservaService.crearReserva(Mockito.any(ReservaRequest.class)))
                .thenReturn(new com.example.restaurApp.entity.Reserva());

        String body = "{\n" +
                "  \"fechaReserva\": \"2025-01-01\",\n" +
                "  \"horaReserva\": \"19:00:00\",\n" +
                "  \"cantidadPersonas\": 2,\n" +
                "  \"nombreCliente\": \"Juan\",\n" +
                "  \"apellidoCliente\": \"Perez\",\n" +
                "  \"correoCliente\": \"juan@mail.com\",\n" +
                "  \"telefonoCliente\": \"111111\"\n" +
                "}";

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }
}


