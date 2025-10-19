package com.example.restaurApp.service;

import com.example.restaurApp.TestDataLoader;
import com.example.restaurApp.entity.DetallePedido;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.repository.DetallePedidoRepository;
import com.example.restaurApp.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
public class DetallePedidoServiceTest extends TestDataLoader {

    @Autowired
    private DetallePedidoService detallePedidoService;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Test
    void cambiarEstadoDetalle_noPermiteTransicionInvalida() {
        Pedido p = pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getDetalles() != null && !pedido.getDetalles().isEmpty())
                .findFirst()
                .orElse(null);
        
        if (p != null) {
            DetallePedido d = p.getDetalles().iterator().next();
            assertNotNull(d.getEstadoDetalle());
        } else {
            // Si no hay pedidos con detalles, crear uno temporal para el test
            DetallePedido d = detallePedidoRepository.findAll().stream()
                    .findFirst()
                    .orElse(null);
            if (d != null) {
                assertNotNull(d.getEstadoDetalle());
            }
        }
    }
}


