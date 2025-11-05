package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.MesaRequest;
import com.example.restaurApp.dto.MesaResponse;
import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.mapper.MesaMapper;
import com.example.restaurApp.repository.EstadoMesaRepository;
import com.example.restaurApp.service.MesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {
    private MesaService mesaService;
    private EstadoMesaRepository estadoMesaRepository;

    public MesaController(MesaService mesaService, EstadoMesaRepository estadoMesaRepository) {
        this.mesaService = mesaService;
        this.estadoMesaRepository = estadoMesaRepository;
    }

    @PostMapping
    public ResponseEntity<MesaResponse> crearMesa(@RequestBody MesaRequest mesaRequest) {
        EstadoMesa estadoMesa = estadoMesaRepository.findById(mesaRequest.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Mesa mesa = MesaMapper.toEntity(mesaRequest, estadoMesa);
        Mesa nuevaMesa = mesaService.crearMesa(mesa);
        return ResponseEntity.status(201).body(MesaMapper.toResponse(nuevaMesa));

    }

    @GetMapping
    public ResponseEntity<List<MesaResponse>> obtenerMesas() {
        List<MesaResponse> mesas = mesaService.ListarMesas()
                .stream()
                .map(MesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/estado/{estadoId}")
    public ResponseEntity<List<MesaResponse>> ListarMesaPorEstado(@PathVariable Long estadoId) {
        List<MesaResponse> mesas = mesaService.ListarMesaPorEstado(estadoId)
                .stream()
                .map(MesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(mesas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> actualizarMesa(@PathVariable Long id,
            @RequestBody MesaRequest mesaRequest) {
        EstadoMesa estadoMesa = estadoMesaRepository.findById(mesaRequest.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Mesa mesa = MesaMapper.toEntity(mesaRequest, estadoMesa);
        try {
            Mesa actualizada = mesaService.actualizarMesa(id, mesa);
            return ResponseEntity.ok(MesaMapper.toResponse(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MesaResponse> eliminarMesa(@PathVariable Long id) {
        try {
            mesaService.eliminarMesa(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
