package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.EstadoPedidoRequest;
import com.example.restaurApp.dto.EstadoPedidoResponse;
import com.example.restaurApp.entity.EstadoPedido;
import com.example.restaurApp.mapper.EstadoPedidoMapper;
import com.example.restaurApp.service.EstadoPedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estadoPedido")

public class EstadoPedidoController {
    private EstadoPedidoService estadoPedidoService;
    public EstadoPedidoController(EstadoPedidoService estadoPedidoService) {
        this.estadoPedidoService = estadoPedidoService;
    }


    @GetMapping
    public ResponseEntity<List<EstadoPedidoResponse>> listarEstadoPedido(){
        List<EstadoPedidoResponse> estadoPedidos = estadoPedidoService.ListarEstadoPedido()
                .stream()
                .map(EstadoPedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(estadoPedidos);
    }

    @GetMapping("/estadoPedido/{id}")
    public ResponseEntity<List<EstadoPedidoResponse>> listarEstadoPedidoPorId(@RequestParam Long id){
        List<EstadoPedidoResponse> estadoPedidos = estadoPedidoService.listasEstadoPedidoPorId(id)
                .stream()
                .map(EstadoPedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(estadoPedidos);

    }

}
