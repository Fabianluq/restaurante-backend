package com.example.restaurApp.service;

import com.example.restaurApp.dto.ReporteVentasRequest;
import com.example.restaurApp.dto.ReporteVentasResponse;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.*;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EmpleadoUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteService {
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final JwtUtil jwtUtil;

    public ReporteService(PagoRepository pagoRepository, PedidoRepository pedidoRepository,
                         DetallePedidoRepository detallePedidoRepository, CategoriaRepository categoriaRepository,
                         EmpleadoRepository empleadoRepository, JwtUtil jwtUtil) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.categoriaRepository = categoriaRepository;
        this.empleadoRepository = empleadoRepository;
        this.jwtUtil = jwtUtil;
    }

    public ReporteVentasResponse generarReporteVentas(ReporteVentasRequest request, String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo y sea administrador
        EmpleadoUtil.validarRolEmpleado(empleado, "ADMIN");

        // Validar fechas
        if (request.getFechaInicio() == null || request.getFechaFin() == null) {
            throw new Validacion("Las fechas de inicio y fin son obligatorias.");
        }

        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new Validacion("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // Obtener pagos en el rango de fechas
        List<Pago> pagos = obtenerPagosEnRango(request.getFechaInicio(), request.getFechaFin());
        
        // Calcular métricas básicas
        BigDecimal totalVentas = pagos.stream()
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalPedidos = pagos.size();
        int totalPagos = pagos.size();
        
        BigDecimal promedioPorPedido = totalPedidos > 0 ? 
            totalVentas.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;

        // Crear respuesta base
        ReporteVentasResponse response = new ReporteVentasResponse(
            request.getFechaInicio(),
            request.getFechaFin(),
            request.getTipoReporte(),
            totalVentas,
            totalPedidos,
            totalPagos,
            promedioPorPedido
        );

        // Generar ventas por día
        response.setVentasPorDia(generarVentasPorDia(request.getFechaInicio(), request.getFechaFin(), pagos));

        // Generar ventas por categoría
        response.setVentasPorCategoria(generarVentaPorCategoria(request.getFechaInicio(), request.getFechaFin()));

        // Generar ventas por empleado
        response.setVentasPorEmpleado(generarVentasPorEmpleado(request.getFechaInicio(), request.getFechaFin()));

        return response;
    }

    private List<Pago> obtenerPagosEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return pagoRepository.findAll().stream()
            .filter(pago -> {
                LocalDate fechaPago = pago.getFechaPago().toLocalDate();
                return !fechaPago.isBefore(fechaInicio) && !fechaPago.isAfter(fechaFin);
            })
            .collect(Collectors.toList());
    }

    private List<ReporteVentasResponse.VentaPorDia> generarVentasPorDia(LocalDate fechaInicio, LocalDate fechaFin, List<Pago> pagos) {
        List<ReporteVentasResponse.VentaPorDia> ventasPorDia = new ArrayList<>();
        
        // Agrupar pagos por fecha
        Map<LocalDate, List<Pago>> pagosPorFecha = pagos.stream()
            .collect(Collectors.groupingBy(pago -> pago.getFechaPago().toLocalDate()));

        // Generar datos para cada día en el rango
        LocalDate fechaActual = fechaInicio;
        while (!fechaActual.isAfter(fechaFin)) {
            List<Pago> pagosDelDia = pagosPorFecha.getOrDefault(fechaActual, new ArrayList<>());
            
            BigDecimal totalVentasDelDia = pagosDelDia.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int totalPedidosDelDia = pagosDelDia.size();
            
            ventasPorDia.add(new ReporteVentasResponse.VentaPorDia(
                fechaActual,
                totalVentasDelDia,
                totalPedidosDelDia
            ));
            
            fechaActual = fechaActual.plusDays(1);
        }
        
        return ventasPorDia;
    }

    private List<ReporteVentasResponse.VentaPorCategoria> generarVentaPorCategoria(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener todos los detalles de pedidos pagados en el rango de fechas
        List<DetallePedido> detalles = detallePedidoRepository.findAll().stream()
            .filter(detalle -> {
                Pedido pedido = detalle.getPedido();
                if (pedido == null || pedido.getEstadoPedido() == null) {
                    return false;
                }
                
                // Solo incluir detalles de pedidos pagados
                if (!"Pagado".equals(pedido.getEstadoPedido().getDescripcion())) {
                    return false;
                }
                
                // Verificar que el pedido esté en el rango de fechas
                LocalDate fechaPedido = pedido.getFechaPedido();
                return !fechaPedido.isBefore(fechaInicio) && !fechaPedido.isAfter(fechaFin);
            })
            .collect(Collectors.toList());

        // Agrupar por categoría
        Map<String, List<DetallePedido>> detallesPorCategoria = detalles.stream()
            .filter(detalle -> detalle.getProducto() != null && detalle.getProducto().getCategoria() != null)
            .collect(Collectors.groupingBy(detalle -> detalle.getProducto().getCategoria().getDescripcion()));

        return detallesPorCategoria.entrySet().stream()
            .map(entry -> {
                String categoria = entry.getKey();
                List<DetallePedido> detallesCategoria = entry.getValue();
                
        BigDecimal totalVentas = detallesCategoria.stream()
            .map(detalle -> BigDecimal.valueOf(detalle.getPrecioUnitario()).multiply(BigDecimal.valueOf(detalle.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                int totalProductos = detallesCategoria.size();
                int totalCantidad = detallesCategoria.stream()
                    .mapToInt(DetallePedido::getCantidad)
                    .sum();
                
                return new ReporteVentasResponse.VentaPorCategoria(
                    categoria,
                    totalVentas,
                    totalProductos,
                    totalCantidad
                );
            })
            .collect(Collectors.toList());
    }

    private List<ReporteVentasResponse.VentaPorEmpleado> generarVentasPorEmpleado(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener pedidos pagados en el rango de fechas
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
            .filter(pedido -> {
                if (pedido.getEstadoPedido() == null) {
                    return false;
                }
                
                // Solo incluir pedidos pagados
                if (!"Pagado".equals(pedido.getEstadoPedido().getDescripcion())) {
                    return false;
                }
                
                // Verificar que el pedido esté en el rango de fechas
                LocalDate fechaPedido = pedido.getFechaPedido();
                return !fechaPedido.isBefore(fechaInicio) && !fechaPedido.isAfter(fechaFin);
            })
            .collect(Collectors.toList());

        // Agrupar por empleado
        Map<String, List<Pedido>> pedidosPorEmpleado = pedidos.stream()
            .filter(pedido -> pedido.getEmpleado() != null)
            .collect(Collectors.groupingBy(pedido -> pedido.getEmpleado().getCorreo()));

        return pedidosPorEmpleado.entrySet().stream()
            .map(entry -> {
                String correoEmpleado = entry.getKey();
                List<Pedido> pedidosEmpleado = entry.getValue();
                
                // Obtener información del empleado
                Empleado empleado = empleadoRepository.findByCorreo(correoEmpleado).orElse(null);
                String nombreEmpleado = empleado != null ? 
                    empleado.getNombre() + " " + empleado.getApellido() : 
                    "Empleado no encontrado";
                String rol = empleado != null && empleado.getRol() != null ? 
                    empleado.getRol().getDescripcion() : 
                    "Sin rol";
                
                // Calcular total de ventas del empleado
                BigDecimal totalVentas = pedidosEmpleado.stream()
                    .flatMap(pedido -> pedido.getDetalles().stream())
                    .map(detalle -> BigDecimal.valueOf(detalle.getPrecioUnitario()).multiply(BigDecimal.valueOf(detalle.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                int totalPedidos = pedidosEmpleado.size();
                
                return new ReporteVentasResponse.VentaPorEmpleado(
                    nombreEmpleado,
                    rol,
                    totalVentas,
                    totalPedidos
                );
            })
            .collect(Collectors.toList());
    }
}
