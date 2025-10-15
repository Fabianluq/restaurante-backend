package com.example.restaurApp.service;

import com.example.restaurApp.TestDataLoader;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.MesaRepository;
import com.example.restaurApp.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class ReglasPedidoServiceTest extends TestDataLoader {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private MesaRepository mesaRepository;

    @Test
    void unaOrdenActivaPorMesa_validacionRepositorio() {
        Mesa mesa = mesaRepository.findAll().get(0);
        boolean existe = pedidoRepository.existsByMesa_IdAndEstadoPedido_DescripcionIn(
                mesa.getId(), List.of("Pendiente", "En preparación")
        );
        assertTrue(existe);
    }
}


