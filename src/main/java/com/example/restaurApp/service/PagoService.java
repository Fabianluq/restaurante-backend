package com.example.restaurApp.service;

import com.example.restaurApp.dto.PagoRequest;
import com.example.restaurApp.dto.PagoResponse;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.mapper.PagoMapper;
import com.example.restaurApp.repository.*;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EmpleadoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final EstadoMesaRepository estadoMesaRepository;
    private final MesaRepository mesaRepository;
    private final PagoMapper pagoMapper;
    private final JwtUtil jwtUtil;

    public PagoService(PagoRepository pagoRepository, PedidoRepository pedidoRepository,
                      EstadoPedidoRepository estadoPedidoRepository, EstadoMesaRepository estadoMesaRepository,
                      MesaRepository mesaRepository, PagoMapper pagoMapper, JwtUtil jwtUtil) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.estadoMesaRepository = estadoMesaRepository;
        this.mesaRepository = mesaRepository;
        this.pagoMapper = pagoMapper;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public PagoResponse procesarPago(PagoRequest request, String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        // Validar que el pedido existe
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
            .orElseThrow(() -> new Validacion("Pedido no encontrado."));

        // Validar que el pedido está en estado válido para cobrar (LISTO, ENTREGADO, PREPARADO)
        String estadoPedido = pedido.getEstadoPedido().getDescripcion().toUpperCase();
        boolean estadoValido = estadoPedido.contains("LISTO") || 
                              estadoPedido.contains("ENTREGADO") || 
                              estadoPedido.contains("PREPARADO");
        
        if (!estadoValido) {
            throw new Validacion("Solo se pueden procesar pagos de pedidos que estén LISTOS, ENTREGADOS o PREPARADOS. " +
                                "Estado actual: " + pedido.getEstadoPedido().getDescripcion());
        }

        // Validar que no existe ya un pago para este pedido
        if (pagoRepository.existsByPedido_Id(request.getPedidoId())) {
            throw new Validacion("Este pedido ya ha sido pagado.");
        }

        // Calcular el total del pedido
        BigDecimal totalCalculado = calcularTotalPedido(pedido);
        
        // Validar que el monto coincida con el total calculado
        if (request.getMonto().compareTo(totalCalculado) != 0) {
            throw new Validacion("El monto del pago (" + request.getMonto() + 
                               ") no coincide con el total del pedido (" + totalCalculado + ").");
        }

        // Validar método de pago
        if (!esMetodoPagoValido(request.getMetodoPago())) {
            throw new Validacion("Método de pago inválido. Use: efectivo, tarjeta o transferencia.");
        }

        // Crear el pago
        Pago pago = new Pago(pedido, request.getMonto(), request.getMetodoPago(), request.getObservaciones());
        pago = pagoRepository.save(pago);

        // Cambiar estado del pedido a "Pagado"
        cambiarEstadoPedidoAPagado(pedido);

        // Liberar la mesa automáticamente
        liberarMesa(pedido);

        return pagoMapper.toResponse(pago);
    }

    public List<PagoResponse> listarPagos(String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        List<Pago> pagos = pagoRepository.findAll();
        return pagos.stream()
            .map(pagoMapper::toResponse)
            .collect(Collectors.toList());
    }

    public PagoResponse buscarPagoPorId(Long id, String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        Pago pago = pagoRepository.findById(id)
            .orElseThrow(() -> new Validacion("Pago no encontrado."));

        return pagoMapper.toResponse(pago);
    }

    public List<PagoResponse> buscarPagosPorPedido(Long pedidoId, String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo
        EmpleadoUtil.validarEmpleadoActivo(empleado);

        List<Pago> pagos = pagoRepository.findByPedido_Id(pedidoId);
        return pagos.stream()
            .map(pagoMapper::toResponse)
            .collect(Collectors.toList());
    }

    private BigDecimal calcularTotalPedido(Pedido pedido) {
        return pedido.getDetalles().stream()
            .map(detalle -> BigDecimal.valueOf(detalle.getPrecioUnitario()).multiply(BigDecimal.valueOf(detalle.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean esMetodoPagoValido(String metodoPago) {
        return metodoPago != null && 
               (metodoPago.equalsIgnoreCase("efectivo") || 
                metodoPago.equalsIgnoreCase("tarjeta") || 
                metodoPago.equalsIgnoreCase("transferencia"));
    }

    @Transactional
    private void cambiarEstadoPedidoAPagado(Pedido pedido) {
        EstadoPedido estadoPagado = estadoPedidoRepository.findByDescripcionIgnoreCase("Pagado")
            .orElseThrow(() -> new Validacion("Estado 'Pagado' no encontrado."));
        
        pedido.setEstadoPedido(estadoPagado);
        pedidoRepository.save(pedido);
    }

    @Transactional
    private void liberarMesa(Pedido pedido) {
        // Solo liberar mesa si el pedido tiene mesa asignada (no es para llevar)
        if (pedido.getMesa() != null) {
            Mesa mesa = pedido.getMesa();
            
            // Verificar que no hay otros pedidos activos en la mesa (excluyendo el pedido actual que ya está pagado)
            List<Pedido> otrosPedidosActivos = pedidoRepository.findByMesa_IdAndEstadoPedido_DescripcionIn(
                mesa.getId(), List.of("Pendiente", "En preparación", "Listo", "Entregado")
            );
            
            // Filtrar para excluir el pedido actual
            boolean tieneOtrosPedidosActivos = otrosPedidosActivos.stream()
                .anyMatch(p -> !p.getId().equals(pedido.getId()));
            
            if (!tieneOtrosPedidosActivos) {
                EstadoMesa estadoDisponible = estadoMesaRepository.findByDescripcionIgnoreCase("Disponible")
                    .orElseThrow(() -> new Validacion("Estado de mesa 'Disponible' no encontrado."));
                
                mesa.setEstado(estadoDisponible);
                mesaRepository.save(mesa);
            }
        }
    }
}
