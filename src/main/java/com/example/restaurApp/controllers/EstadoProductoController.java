package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.EstadoProductoRequest;
import com.example.restaurApp.dto.EstadoProductoResponse;
import com.example.restaurApp.entity.EstadoProducto;
import com.example.restaurApp.mapper.EstadoProductoMapper;
import com.example.restaurApp.service.EstadoProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estadoProducto")
public class EstadoProductoController {
    private EstadoProductoService estadoProductoService;
    public EstadoProductoController(EstadoProductoService estadoProductoService) {
        this.estadoProductoService = estadoProductoService;
    }

    @GetMapping
    public ResponseEntity<List<EstadoProductoResponse>> listarEstadoProducto(){
        List<EstadoProductoResponse> estadoProductos = estadoProductoService.ListarEstadoProductos()
                .stream()
                .map(EstadoProductoMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(estadoProductos);
    }

    @GetMapping("/estadoProducto/{id}")
    public ResponseEntity<List<EstadoProductoResponse>> listarEstadoProductoPorId(@RequestParam Long id){
        List<EstadoProductoResponse> estadoProductos = estadoProductoService.ListarEstadoPrductosPorId(id)
                .stream()
                .map(EstadoProductoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(estadoProductos);

    }



}
