package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.DetallePedidoRequest;
import com.example.restaurApp.dto.DetallePedidoResponse;
import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.entity.DetallePedido;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.mapper.DetallePedidoMapper;
import com.example.restaurApp.repository.PedidoRepository;
import com.example.restaurApp.repository.ProductoRepository;
import com.example.restaurApp.service.DetallePedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-pedido")
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

    @PostMapping("/{pedidoId}/detalles")
    @PreAuthorize("hasAnyRole('MESERO','ADMIN')")
    public ResponseEntity<ApiResponse<DetallePedidoResponse>> agregarProducto(
            @PathVariable Long pedidoId,
            @Valid @RequestBody DetallePedidoRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        DetallePedido nuevoDetalle = detallePedidoService.agregarProducto(pedidoId, request, token);
        DetallePedidoResponse response = DetallePedidoMapper.toResponse(nuevoDetalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Producto agregado al pedido exitosamente", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public ResponseEntity<ApiResponse<List<DetallePedidoResponse>>> listarDetalles() {
        List<DetallePedidoResponse> detalles = detallePedidoService.listarDetalles()
                .stream()
                .map(DetallePedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Detalles listados exitosamente", detalles));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public ResponseEntity<ApiResponse<DetallePedidoResponse>> buscarPorId(@PathVariable Long id) {
        return detallePedidoService.buscarPorId(id)
                .map(DetallePedidoMapper::toResponse)
                .map(response -> ResponseEntity.ok(ApiResponse.success("Detalle encontrado", response)))
                .orElse(ResponseEntity.ok(ApiResponse.notFound("Detalle no encontrado")));
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public ResponseEntity<ApiResponse<List<DetallePedidoResponse>>> listarPorPedido(@PathVariable Long pedidoId) {
        List<DetallePedidoResponse> detalles = detallePedidoService.listarPorPedido(pedidoId)
                .stream()
                .map(DetallePedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Detalles del pedido listados exitosamente", detalles));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MESERO','ADMIN')")
    public ResponseEntity<ApiResponse<DetallePedidoResponse>> actualizarDetalle(
            @PathVariable Long id,
            @Valid @RequestBody DetallePedidoRequest request,
            @RequestHeader("Authorization") String token) {

        String jwtToken = token.replace("Bearer ", "");
        DetallePedido detalleActualizado = detallePedidoService.actualizarDetalle(id, request, jwtToken);
        DetallePedidoResponse response = DetallePedidoMapper.toResponse(detalleActualizado);
        return ResponseEntity.ok(ApiResponse.success("Detalle actualizado exitosamente", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MESERO','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalle(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        // Quitar el prefijo "Bearer "
        String jwt = token.replace("Bearer ", "");
        detallePedidoService.eliminarDetalle(id, jwt);
        return ResponseEntity.ok(ApiResponse.success("Detalle eliminado correctamente y total del pedido actualizado", null));
    }

    @PutMapping("/{id}/estado/{estadoDetalleId}")
    @PreAuthorize("hasAnyRole('COCINERO','ADMIN')")
    public ResponseEntity<ApiResponse<DetallePedidoResponse>> cambiarEstadoDetalle(
            @PathVariable Long id,
            @PathVariable Long estadoDetalleId,
            @RequestHeader("Authorization") String token) {

        // Quitar el prefijo "Bearer "
        String jwt = token.replace("Bearer ", "");
        DetallePedido detalleActualizado = detallePedidoService.cambiarEstadoDetalle(id, estadoDetalleId, jwt);
        DetallePedidoResponse response = DetallePedidoMapper.toResponse(detalleActualizado);
        return ResponseEntity.ok(ApiResponse.success("Estado del detalle cambiado exitosamente", response));
    }

}
