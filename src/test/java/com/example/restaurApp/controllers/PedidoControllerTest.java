package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.dto.PedidoRequest;
import com.example.restaurApp.dto.PedidoResponse;
import com.example.restaurApp.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void listarPedidos_debePermitirAdmin() throws Exception {
        Mockito.when(pedidoService.listarPedidos()).thenReturn(List.of());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = {"MESERO"})
    void crearPedido_debePermitirMesero() throws Exception {
        Mockito.when(pedidoService.crearPedido(Mockito.any(PedidoRequest.class), Mockito.anyString()))
                .thenReturn(Mockito.mock(com.example.restaurApp.entity.Pedido.class));

        String body = "{\n" +
                "  \"estadoId\": 1,\n" +
                "  \"mesaId\": 1,\n" +
                "  \"paraLlevar\": false\n" +
                "}";

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void listarPedidos_anonimoDebeFallar() throws Exception {
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isUnauthorized());
    }
}


