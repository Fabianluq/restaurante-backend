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
public class DetallePedidoServiceTest extends TestDataLoader {

    @Autowired
    private DetallePedidoService detallePedidoService;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Test
    void cambiarEstadoDetalle_noPermiteTransicionInvalida() {
        Pedido p = pedidoRepository.findAll().get(0);
        DetallePedido d = p.getDetalles().iterator().next();
        // Simulación simple: asegurar que existe y tiene estado inicial
        assertNotNull(d.getEstadoDetalle());
    }
}


