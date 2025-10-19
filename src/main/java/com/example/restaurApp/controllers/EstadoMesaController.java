package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.EstadoMesaRequest;
import com.example.restaurApp.dto.EstadoMesaResponse;
import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.mapper.EstadoMesaMapper;
import com.example.restaurApp.service.EstadoMesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estadoMesa")
public class EstadoMesaController {
    private EstadoMesaService estadoMesaService;

    public EstadoMesaController(EstadoMesaService estadoMesaService) {
        this.estadoMesaService = estadoMesaService;
    }


    @GetMapping
    public ResponseEntity<List<EstadoMesaResponse>> listarEstadoMesa() {
        List<EstadoMesaResponse> estadoMesas = estadoMesaService.ListarEstadoMesa()
                .stream()
                .map(EstadoMesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(estadoMesas);
    }

    @GetMapping("/estadoMesa/{id}")
    public ResponseEntity<List<EstadoMesaResponse>> listarEstadoMesaPorId(@RequestParam Long id) {
        List<EstadoMesaResponse> estadoMesas = estadoMesaService.listasEstadoMesasPorId(id)
                .stream()
                .map(EstadoMesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(estadoMesas);

    }

}
