package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.DetallePedidoRequest;
import com.example.restaurApp.dto.DetallePedidoResponse;
import com.example.restaurApp.entity.DetallePedido;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.entity.Producto;
import com.example.restaurApp.mapper.DetallePedidoMapper;
import com.example.restaurApp.repository.PedidoRepository;
import com.example.restaurApp.repository.ProductoRepository;
import com.example.restaurApp.service.DetallePedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles")
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public DetallePedidoController(DetallePedidoService detallePedidoService,
                                   PedidoRepository pedidoRepository,
                                   ProductoRepository productoRepository) {
        this.detallePedidoService = detallePedidoService;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @PostMapping
    public ResponseEntity<DetallePedidoResponse> crearDetalle(@RequestBody DetallePedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetallePedido detalle = DetallePedidoMapper.toEntity(request, pedido, producto);
        DetallePedido nuevo = detallePedidoService.crearDetalle(detalle);

        return ResponseEntity.status(201).body(DetallePedidoMapper.toResponse(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<DetallePedidoResponse>> listarDetalles() {
        List<DetallePedidoResponse> detalles = detallePedidoService.listarDetalles()
                .stream()
                .map(DetallePedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponse> buscarPorId(@PathVariable Long id) {
        return detallePedidoService.buscarPorId(id)
                .map(DetallePedidoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<DetallePedidoResponse>> listarPorPedido(@PathVariable Long pedidoId) {
        List<DetallePedidoResponse> detalles = detallePedidoService.listarPorPedido(pedidoId)
                .stream()
                .map(DetallePedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(detalles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedidoResponse> actualizarDetalle(@PathVariable Long id,
                                                                   @RequestBody DetallePedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetallePedido detalle = DetallePedidoMapper.toEntity(request, pedido, producto);
        DetallePedido actualizado = detallePedidoService.actualizarDetalle(id, detalle);

        return ResponseEntity.ok(DetallePedidoMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        try {
            detallePedidoService.eliminarDetalle(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
