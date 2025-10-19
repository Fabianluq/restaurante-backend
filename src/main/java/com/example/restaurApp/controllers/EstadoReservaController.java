package com.example.restaurApp.controllers;


import com.example.restaurApp.dto.EstadoReservaRequest;
import com.example.restaurApp.dto.EstadoReservaResponse;
import com.example.restaurApp.entity.EstadoReserva;
import com.example.restaurApp.mapper.EstadoReservaMapper;
import com.example.restaurApp.service.EstadoReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estadoReserva")

public class EstadoReservaController {
    private EstadoReservaService estadoReservaService;
    public EstadoReservaController(EstadoReservaService estadoReservaService) {
        this.estadoReservaService = estadoReservaService;
    }

    @GetMapping
    public ResponseEntity<List<EstadoReservaResponse>> listarEstadoReserva(){
        List<EstadoReservaResponse> estadoPedidos = estadoReservaService.ListarEstadoReserva()
                .stream()
                .map(EstadoReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(estadoPedidos);
    }

    @GetMapping("/estadoReserva/{id}")
    public ResponseEntity<List<EstadoReservaResponse>> listarEstadoReservaPorId(@RequestParam Long id){
        List<EstadoReservaResponse> estadoReservas = estadoReservaService.listasEstadoReservaPorId(id)
                .stream()
                .map(EstadoReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(estadoReservas);

    }

}

