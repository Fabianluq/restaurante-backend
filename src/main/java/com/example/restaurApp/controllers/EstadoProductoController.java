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

    @PostMapping
    public ResponseEntity<EstadoProductoResponse> crearEstadoProducto(@RequestBody EstadoProductoRequest estadoProductoRequest){
        EstadoProducto estadoProducto = EstadoProductoMapper.toEntity(estadoProductoRequest);
        EstadoProducto nuevoEstadoProducto = estadoProductoService.crearEstadoProducto(estadoProducto);
        return ResponseEntity.ok().body(EstadoProductoMapper.toResponse(nuevoEstadoProducto));

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

    @PutMapping("/{id}")
    public ResponseEntity<EstadoProductoResponse> actualizarEstadoProducto(@RequestBody Long id, @RequestBody EstadoProductoRequest estadoProductoRequest){
        EstadoProducto estadoProducto = EstadoProductoMapper.toEntity(estadoProductoRequest);
        try {
            EstadoProducto actualizado = estadoProductoService.actualizarEstadoProducto(id, estadoProducto);
            return ResponseEntity.ok(EstadoProductoMapper.toResponse(actualizado));
        } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarEstadoProducto(@PathVariable Long id){
        try{
            estadoProductoService.eliminarEstadoProducto(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

}
