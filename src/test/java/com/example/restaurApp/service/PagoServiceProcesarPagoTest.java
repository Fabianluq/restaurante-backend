package com.example.restaurApp.service;

import com.example.restaurApp.TestDataLoader;
import com.example.restaurApp.dto.PagoRequest;
import com.example.restaurApp.dto.PagoResponse;
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.entity.EstadoPedido;
import com.example.restaurApp.repository.PedidoRepository;
import com.example.restaurApp.repository.EstadoPedidoRepository;
import com.example.restaurApp.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PagoServiceProcesarPagoTest extends TestDataLoader {

    @Autowired
    private PagoService pagoService;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private EstadoPedidoRepository estadoPedidoRepository;

    @MockBean
    private JwtUtil jwtUtil;

    private String bearer;

    @BeforeEach
    void setupJwt() {
        bearer = "Bearer faketoken";
        Empleado emp = new Empleado();
        emp.setNombre("Tester");
        emp.setApellido("Unit");
        emp.setCorreo("tester@rest.com");
        emp.setContrasenia("hash");
        emp.setActivo(true);
        Mockito.when(jwtUtil.extractUsername(Mockito.any())).thenReturn(emp.getCorreo());
        Mockito.when(jwtUtil.getEmpleadoFromToken(Mockito.any())).thenReturn(emp);
    }

    @Test
    void procesarPago_exitosoCuandoEntregadoYMontoCoincide() {
        Pedido pedido = pedidoRepository.findAll().get(0);
        EstadoPedido entregado = estadoPedidoRepository.findByDescripcionIgnoreCase("Entregado").orElseThrow();
        pedido.setEstadoPedido(entregado);
        pedidoRepository.save(pedido);

        BigDecimal total = pedido.getDetalles().stream()
                .map(d -> BigDecimal.valueOf(d.getPrecioUnitario()).multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PagoRequest req = new PagoRequest(pedido.getId(), total, "efectivo", null);
        PagoResponse resp = pagoService.procesarPago(req, bearer);
        assertNotNull(resp);
        assertEquals(pedido.getId(), resp.getPedidoId());
    }
}


