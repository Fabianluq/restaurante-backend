package com.example.restaurApp.service;

import com.example.restaurApp.TestDataLoader;
import com.example.restaurApp.dto.PagoRequest;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class PagoServiceTest extends TestDataLoader {

    @Autowired
    private PagoService pagoService;
    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void calcularTotalPedido_paraCompararMonto() {
        Pedido pedido = pedidoRepository.findAll().get(0);
        BigDecimal totalEsperado = pedido.getDetalles().stream()
                .map(d -> BigDecimal.valueOf(d.getPrecioUnitario()).multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertTrue(totalEsperado.compareTo(BigDecimal.ZERO) > 0);
    }
}


