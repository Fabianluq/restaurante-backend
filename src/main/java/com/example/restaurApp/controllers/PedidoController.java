package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.PedidoRequest;
import com.example.restaurApp.dto.PedidoResponse;
import com.example.restaurApp.dto.ApiResponse;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.mapper.PedidoMapper;
import com.example.restaurApp.repository.ClienteRepository;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.repository.EstadoPedidoRepository;
import com.example.restaurApp.repository.MesaRepository;
import com.example.restaurApp.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final MesaRepository mesaRepository;
    private final ClienteRepository clienteRepository;

    public PedidoController(PedidoService pedidoService,
                            EstadoPedidoRepository estadoPedidoRepository,
                            EmpleadoRepository empleadoRepository,
                            MesaRepository mesaRepository,
                            ClienteRepository clienteRepository) {
        this.pedidoService = pedidoService;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.empleadoRepository = empleadoRepository;
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MESERO','ADMIN')")
    public ResponseEntity<ApiResponse<PedidoResponse>> crearPedido(
            @Valid @RequestBody PedidoRequest request,
            @RequestHeader("Authorization") String token) {

        // El token viene como "Bearer eyJhbGciOiJIUzI1..."
        token = token.replace("Bearer ", "");

        Pedido nuevoPedido = pedidoService.crearPedido(request, token);
        PedidoResponse response = PedidoMapper.toResponse(nuevoPedido);
        return ResponseEntity.status(201).body(ApiResponse.created("Pedido creado exitosamente", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPedidos() {
        List<PedidoResponse> pedidos = pedidoService.listarPedidos()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Pedidos listados exitosamente", pedidos));
    }

    @GetMapping("/estado/{estadoId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPorEstado(@PathVariable Long estadoId) {
        List<PedidoResponse> pedidos = pedidoService.listarPedidosPorEstado(estadoId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Pedidos listados por estado", pedidos));
    }

    @GetMapping("/empleado/{empleadoId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPorEmpleado(@PathVariable Long empleadoId) {
        List<PedidoResponse> pedidos = pedidoService.listarPedidosPorEmpleado(empleadoId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Pedidos listados por empleado", pedidos));
    }

    @GetMapping("/mesa/{mesaId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPorMesa(@PathVariable Long mesaId) {
        List<PedidoResponse> pedidos = pedidoService.listarPedidosPorMesa(mesaId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Pedidos listados por mesa", pedidos));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPorFechaOhoraOEstado(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalTime hora,
            @RequestParam(required = false) Long estadoId) {

        List<PedidoResponse> pedidos = pedidoService.listarPorFechaOhoraOEstado(fecha, hora, estadoId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Pedidos encontrados", pedidos));
    }

    @GetMapping("/cocina")
    @PreAuthorize("hasAnyRole('COCINERO','ADMIN')")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPedidosParaCocina(
            @RequestHeader("Authorization") String token) {
        // Extraer el token sin el prefijo "Bearer "
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        List<PedidoResponse> pedidos = pedidoService.listarPedidosParaCocina(token);
        return ResponseEntity.ok(ApiResponse.success("Pedidos para cocina listados exitosamente", pedidos));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MESERO','ADMIN')")
    public ResponseEntity<ApiResponse<PedidoResponse>> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequest request,
            @RequestHeader("Authorization") String token) {

        // Eliminar el prefijo "Bearer " del token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Pedido pedidoActualizado = pedidoService.actualizarPedido(id, request, token);
        PedidoResponse response = PedidoMapper.toResponse(pedidoActualizado);
        return ResponseEntity.ok(ApiResponse.success("Pedido actualizado exitosamente", response));
    }

    @PutMapping("/{id}/estado/{idEstado}")
    @PreAuthorize("hasAnyRole('COCINERO','MESERO','ADMIN')")
    public ResponseEntity<ApiResponse<PedidoResponse>> cambiarEstado(
            @PathVariable Long id,
            @PathVariable Long idEstado,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Pedido pedidoActualizado = pedidoService.cambiarEstado(id, idEstado, token);
        PedidoResponse response = PedidoMapper.toResponse(pedidoActualizado);
        return ResponseEntity.ok(ApiResponse.success("Estado del pedido cambiado exitosamente", response));
    }

    @GetMapping("/{id}/total")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public ResponseEntity<ApiResponse<BigDecimal>> calcularTotalPedido(@PathVariable Long id) {
        BigDecimal total = pedidoService.calcularTotalPedido(id);
        return ResponseEntity.ok(ApiResponse.success("Total calculado exitosamente", total));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.ok(ApiResponse.success("Pedido eliminado exitosamente", null));
    }
}
