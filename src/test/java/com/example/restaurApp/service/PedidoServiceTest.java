package com.example.restaurApp.service;

import com.example.restaurApp.TestDataLoader;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
public class PedidoServiceTest extends TestDataLoader {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void calcularTotalPedido_debeSumarDetalles() {
        Pedido pedido = pedidoRepository.findAll().stream()
                .filter(p -> p.getDetalles() != null && !p.getDetalles().isEmpty())
                .findFirst().orElseGet(() -> pedidoRepository.findAll().get(0));
        BigDecimal total = pedidoService.calcularTotalPedido(pedido.getId());
        assertNotNull(total);
        assertTrue(total.compareTo(BigDecimal.ZERO) >= 0);
    }
}


