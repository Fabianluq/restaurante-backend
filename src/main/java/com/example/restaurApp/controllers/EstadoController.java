package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.EstadoPedidoResponse;
import com.example.restaurApp.mapper.EstadoPedidoMapper;
import com.example.restaurApp.service.EstadoPedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para manejar endpoints genéricos de estados
 * Proporciona endpoints como /estados/pedidos para compatibilidad con el frontend
 */
@RestController
@RequestMapping("/estados")
public class EstadoController {

    private final EstadoPedidoService estadoPedidoService;

    public EstadoController(EstadoPedidoService estadoPedidoService) {
        this.estadoPedidoService = estadoPedidoService;
    }

    /**
     * Endpoint para obtener estados de pedidos
     * Compatible con el frontend que busca /estados/pedidos
     */
    @GetMapping("/pedidos")
    public ResponseEntity<List<EstadoPedidoResponse>> listarEstadosPedidos() {
        List<EstadoPedidoResponse> estadoPedidos = estadoPedidoService.ListarEstadoPedido()
                .stream()
                .map(EstadoPedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(estadoPedidos);
    }
}

