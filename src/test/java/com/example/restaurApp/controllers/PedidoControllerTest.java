package com.example.restaurApp.controllers;

import com.example.restaurApp.config.TestSecurityConfig;
import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.dto.PedidoRequest;
import com.example.restaurApp.dto.PedidoResponse;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = PedidoController.class)
@Import(TestSecurityConfig.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    // Repositorios requeridos por el constructor de PedidoController
    @MockBean private EstadoPedidoRepository estadoPedidoRepository;
    @MockBean private EmpleadoRepository empleadoRepository;
    @MockBean private MesaRepository mesaRepository;
    @MockBean private ClienteRepository clienteRepository;

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
        // Devolver un Pedido completamente poblado para evitar NPE en el mapper
        com.example.restaurApp.entity.Pedido pedido = new com.example.restaurApp.entity.Pedido();
        com.example.restaurApp.entity.EstadoPedido estado = new com.example.restaurApp.entity.EstadoPedido("Pendiente");
        com.example.restaurApp.entity.Empleado emp = new com.example.restaurApp.entity.Empleado();
        emp.setNombre("Ana"); emp.setApellido("Lopez"); emp.setCorreo("ana@rest.com");
        com.example.restaurApp.entity.Mesa mesa = new com.example.restaurApp.entity.Mesa();
        mesa.setNumero(1); mesa.setCapacidad(4);
        com.example.restaurApp.entity.Cliente cli = new com.example.restaurApp.entity.Cliente("Juan","Perez","j@x.com","111");
        pedido.setEstadoPedido(estado);
        pedido.setEmpleado(emp);
        pedido.setMesa(mesa);
        pedido.setCliente(cli);
        Mockito.when(pedidoService.crearPedido(Mockito.any(PedidoRequest.class), Mockito.anyString()))
                .thenReturn(pedido);

        String body = "{\n" +
                "  \"estadoId\": 1,\n" +
                "  \"mesaId\": 1,\n" +
                "  \"paraLlevar\": false\n" +
                "}";

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .with(csrf())
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void listarPedidos_anonimoDebeFallar() throws Exception {
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isUnauthorized());
    }
}


