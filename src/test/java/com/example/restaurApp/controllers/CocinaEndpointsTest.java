package com.example.restaurApp.controllers;

import com.example.restaurApp.config.TestSecurityConfig;
import com.example.restaurApp.repository.ClienteRepository;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.repository.EstadoPedidoRepository;
import com.example.restaurApp.repository.MesaRepository;
import com.example.restaurApp.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PedidoController.class)
@Import(TestSecurityConfig.class)
class CocinaEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    // Repos requeridos por el controller de Pedido
    @MockBean private EstadoPedidoRepository estadoPedidoRepository;
    @MockBean private EmpleadoRepository empleadoRepository;
    @MockBean private MesaRepository mesaRepository;
    @MockBean private ClienteRepository clienteRepository;

    @Test
    @WithMockUser(roles = {"COCINERO"})
    void listarPedidosParaCocina_permitidoCocinero() throws Exception {
        Mockito.when(pedidoService.listarPedidosParaCocina(Mockito.anyString())).thenReturn(List.of());

        mockMvc.perform(get("/pedidos/cocina").header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void listarPedidosParaCocina_anonimoDebeFallar() throws Exception {
        mockMvc.perform(get("/pedidos/cocina"))
                .andExpect(status().isUnauthorized());
    }
}


