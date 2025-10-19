package com.example.restaurApp.controllers;

import com.example.restaurApp.config.TestSecurityConfig;
import com.example.restaurApp.dto.ReservaRequest;
import com.example.restaurApp.dto.ReservaResponse;
import com.example.restaurApp.service.ReservaService;
import com.example.restaurApp.repository.EstadoReservaRepository;
import com.example.restaurApp.repository.MesaRepository;
import com.example.restaurApp.repository.ClienteRepository;
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

@WebMvcTest(controllers = ReservaController.class)
@Import(TestSecurityConfig.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    // Repositorios requeridos por el constructor del controller
    @MockBean
    private EstadoReservaRepository estadoReservaRepository;

    @MockBean
    private MesaRepository mesaRepository;

    @MockBean
    private ClienteRepository clienteRepository;

    @Test
    @WithMockUser(roles = {"MESERO"})
    void crearReserva_meseroPermitido() throws Exception {
        com.example.restaurApp.entity.Reserva reserva = new com.example.restaurApp.entity.Reserva();
        com.example.restaurApp.entity.Cliente cli = new com.example.restaurApp.entity.Cliente("Juan","Perez","j@x.com","111");
        reserva.setCliente(cli);
        reserva.setEstadoReserva(new com.example.restaurApp.entity.EstadoReserva("Pendiente"));
        Mockito.when(reservaService.crearReserva(Mockito.any(ReservaRequest.class)))
                .thenReturn(reserva);

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
                        .with(csrf())
                        .content(body))
                .andExpect(status().isCreated());
    }
}


