package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.EstadoMesaRequest;
import com.example.restaurApp.dto.EstadoMesaResponse;
import com.example.restaurApp.dto.EstadoProductoRequest;
import com.example.restaurApp.dto.EstadoProductoResponse;
import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.entity.EstadoProducto;
import com.example.restaurApp.mapper.EstadoMesaMapper;
import com.example.restaurApp.mapper.EstadoProductoMapper;
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

    @PostMapping
    public ResponseEntity<EstadoMesaResponse> crearEstadoMesa(@RequestBody EstadoMesaRequest estadoMesaRequest){
        EstadoMesa estadoMesa = EstadoMesaMapper.toEntity(estadoMesaRequest);
        EstadoMesa nuevoEstadoMesa = estadoMesaService.crearEstadoMesa(estadoMesa);
        return ResponseEntity.ok().body(EstadoMesaMapper.toResponse(nuevoEstadoMesa));

    }

    @GetMapping
    public ResponseEntity<List<EstadoMesaResponse>> listarEstadoMesa(){
        List<EstadoMesaResponse> estadoMesas = estadoMesaService.ListarEstadoMesa()
                .stream()
                .map(EstadoMesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(estadoMesas);
    }

    @GetMapping("/estadoMesa/{id}")
    public ResponseEntity<List<EstadoMesaResponse>> listarEstadoMesaPorId(@RequestParam Long id){
        List<EstadoMesaResponse> estadoMesas = estadoMesaService.listasEstadoMesasPorId(id)
                .stream()
                .map(EstadoMesaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(estadoMesas);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoMesaResponse> actualizarEstadoMesa(@RequestBody Long id, @RequestBody EstadoMesaRequest estadoMesaRequest){
        EstadoMesa estadoMesa = EstadoMesaMapper.toEntity(estadoMesaRequest);
        try {
            EstadoMesa actualizado = estadoMesaService.actualizarEstadoMesa(id, estadoMesa);
            return ResponseEntity.ok(EstadoMesaMapper.toResponse(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarEstadoMesa(@PathVariable Long id){
        try{
            estadoMesaService.eliminarEstadoMesa(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

}
