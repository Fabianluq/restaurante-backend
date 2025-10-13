package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.PedidoRequest;
import com.example.restaurApp.dto.PedidoResponse;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.mapper.PedidoMapper;
import com.example.restaurApp.repository.ClienteRepository;
import com.example.restaurApp.repository.EmpleadoRepository;
import com.example.restaurApp.repository.EstadoPedidoRepository;
import com.example.restaurApp.repository.MesaRepository;
import com.example.restaurApp.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
    public ResponseEntity<PedidoResponse> crearPedido(@RequestBody PedidoRequest request) {
        EstadoPedido estado = estadoPedidoRepository.findById(request.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        Mesa mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Pedido pedido = PedidoMapper.toEntity(request, estado, empleado, mesa, cliente);
        Pedido nuevoPedido = pedidoService.crearPedido(pedido);

        return ResponseEntity.ok(PedidoMapper.toResponse(nuevoPedido));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarPedidos() {
        List<PedidoResponse> pedidos = pedidoService.ListarPedidos()
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/estado/{estadoId}")
    public ResponseEntity<List<PedidoResponse>> listarPorEstado(@PathVariable Long estadoId) {
        List<PedidoResponse> pedidos = pedidoService.ListarPedidosPorEstado(estadoId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<PedidoResponse>> listarPorEmpleado(@PathVariable Long empleadoId) {
        List<PedidoResponse> pedidos = pedidoService.ListarPedidosPorEmpleado(empleadoId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<List<PedidoResponse>> listarPorMesa(@PathVariable Long mesaId) {
        List<PedidoResponse> pedidos = pedidoService.ListarPedidosPorMesa(mesaId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PedidoResponse>> ListarPorFechaOhoraOEstado(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalTime hora,
            @RequestParam(required = false) Long estadoId) {

        List<PedidoResponse> pedidos = pedidoService.ListarPorFechaOhoraOEstado(fecha, hora, estadoId)
                .stream()
                .map(PedidoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> actualizarPedido(@PathVariable Long id,
                                                           @RequestBody PedidoRequest request) {
        EstadoPedido estado = estadoPedidoRepository.findById(request.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        Mesa mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Pedido pedido = PedidoMapper.toEntity(request, estado, empleado, mesa, cliente);
        Pedido actualizado = pedidoService.actualizarPedido(id, pedido);

        return ResponseEntity.ok(PedidoMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
