package com.example.restaurApp.service;

import com.example.restaurApp.dto.DashboardResponse;
import com.example.restaurApp.entity.*;
import com.example.restaurApp.excepciones.Validacion;
import com.example.restaurApp.repository.*;
import com.example.restaurApp.security.JwtUtil;
import com.example.restaurApp.util.EmpleadoUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ClienteRepository clienteRepository;
    private final JwtUtil jwtUtil;

    public DashboardService(PagoRepository pagoRepository, PedidoRepository pedidoRepository,
                           ReservaRepository reservaRepository, MesaRepository mesaRepository,
                           DetallePedidoRepository detallePedidoRepository, ClienteRepository clienteRepository,
                           JwtUtil jwtUtil) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.reservaRepository = reservaRepository;
        this.mesaRepository = mesaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.clienteRepository = clienteRepository;
        this.jwtUtil = jwtUtil;
    }

    public DashboardResponse obtenerDashboard(String token) {
        // Validar token y obtener empleado
        String username = jwtUtil.extractUsername(token.substring(7));
        Empleado empleado = jwtUtil.getEmpleadoFromToken(token.substring(7));
        
        if (empleado == null) {
            throw new Validacion("Token inválido o empleado no encontrado.");
        }

        // Validar que el empleado esté activo y sea administrador
        EmpleadoUtil.validarRolEmpleado(empleado, "ADMINISTRADOR");

        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        DashboardResponse dashboard = new DashboardResponse(hoy, ahora);

        // Generar métricas generales
        dashboard.setMetricasGenerales(generarMetricasGenerales(hoy));

        // Generar estado de mesas
        dashboard.setEstadoMesas(generarEstadoMesas());

        // Generar pedidos de hoy
        dashboard.setPedidosHoy(generarPedidosHoy(hoy));

        // Generar top productos
        dashboard.setTopProductos(generarTopProductos(hoy));

        // Generar alertas
        dashboard.setAlertas(generarAlertas(hoy));

        return dashboard;
    }

    private DashboardResponse.MetricasGenerales generarMetricasGenerales(LocalDate hoy) {
        DashboardResponse.MetricasGenerales metricas = new DashboardResponse.MetricasGenerales();

        // Ventas de hoy
        List<Pago> pagosHoy = obtenerPagosPorFecha(hoy);
        metricas.setVentasHoy(calcularTotalVentas(pagosHoy));
        metricas.setPedidosHoy(pagosHoy.size());

        // Ventas de ayer
        LocalDate ayer = hoy.minusDays(1);
        List<Pago> pagosAyer = obtenerPagosPorFecha(ayer);
        metricas.setVentasAyer(calcularTotalVentas(pagosAyer));
        metricas.setPedidosAyer(pagosAyer.size());

        // Ventas de la semana
        LocalDate inicioSemana = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
        List<Pago> pagosSemana = obtenerPagosEnRango(inicioSemana, hoy);
        metricas.setVentasSemana(calcularTotalVentas(pagosSemana));

        // Ventas del mes
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        List<Pago> pagosMes = obtenerPagosEnRango(inicioMes, hoy);
        metricas.setVentasMes(calcularTotalVentas(pagosMes));

        // Reservas de hoy
        List<Reserva> reservasHoy = reservaRepository.findAll().stream()
            .filter(reserva -> reserva.getFechaReserva().equals(hoy))
            .collect(Collectors.toList());
        metricas.setReservasHoy(reservasHoy.size());

        // Clientes nuevos de hoy (simplificado - no tenemos fecha de registro)
        // Por ahora, establecemos en 0 ya que no tenemos el campo fechaRegistro
        metricas.setClientesNuevosHoy(0);

        return metricas;
    }

    private DashboardResponse.EstadoMesas generarEstadoMesas() {
        DashboardResponse.EstadoMesas estadoMesas = new DashboardResponse.EstadoMesas();
        
        List<Mesa> todasLasMesas = mesaRepository.findAll();
        estadoMesas.setTotalMesas(todasLasMesas.size());

        // Contar mesas por estado
        Map<String, Long> mesasPorEstado = todasLasMesas.stream()
            .collect(Collectors.groupingBy(
                mesa -> mesa.getEstado().getDescripcion(),
                Collectors.counting()
            ));

        estadoMesas.setMesasDisponibles(mesasPorEstado.getOrDefault("Disponible", 0L).intValue());
        estadoMesas.setMesasOcupadas(mesasPorEstado.getOrDefault("Ocupada", 0L).intValue());
        estadoMesas.setMesasReservadas(mesasPorEstado.getOrDefault("Reservada", 0L).intValue());
        estadoMesas.setMesasMantenimiento(mesasPorEstado.getOrDefault("Mantenimiento", 0L).intValue());

        // Generar detalle de mesas
        List<DashboardResponse.EstadoMesas.MesaInfo> mesasDetalle = todasLasMesas.stream()
            .map(mesa -> {
                DashboardResponse.EstadoMesas.MesaInfo mesaInfo = new DashboardResponse.EstadoMesas.MesaInfo();
                mesaInfo.setNumero(String.valueOf(mesa.getNumero()));
                mesaInfo.setEstado(mesa.getEstado().getDescripcion());
                
                // Buscar empleado asignado (último pedido activo)
                Pedido ultimoPedido = pedidoRepository.findByMesa_Id(mesa.getId()).stream()
                    .filter(pedido -> !pedido.getEstadoPedido().getDescripcion().equals("Pagado") &&
                                     !pedido.getEstadoPedido().getDescripcion().equals("Cancelado"))
                    .max(Comparator.comparing(Pedido::getHoraPedido))
                    .orElse(null);
                
                if (ultimoPedido != null && ultimoPedido.getEmpleado() != null) {
                    mesaInfo.setEmpleadoAsignado(ultimoPedido.getEmpleado().getNombre() + " " + 
                                               ultimoPedido.getEmpleado().getApellido());
                    mesaInfo.setUltimaActividad(ultimoPedido.getHoraPedido().atDate(ultimoPedido.getFechaPedido()));
                }
                
                return mesaInfo;
            })
            .collect(Collectors.toList());

        estadoMesas.setMesasDetalle(mesasDetalle);
        return estadoMesas;
    }

    private DashboardResponse.PedidosHoy generarPedidosHoy(LocalDate hoy) {
        DashboardResponse.PedidosHoy pedidosHoy = new DashboardResponse.PedidosHoy();
        
        List<Pedido> pedidosDelDia = pedidoRepository.findAll().stream()
            .filter(pedido -> pedido.getFechaPedido().equals(hoy))
            .collect(Collectors.toList());

        pedidosHoy.setTotalPedidos(pedidosDelDia.size());

        // Contar pedidos por estado
        Map<String, Long> pedidosPorEstado = pedidosDelDia.stream()
            .collect(Collectors.groupingBy(
                pedido -> pedido.getEstadoPedido().getDescripcion(),
                Collectors.counting()
            ));

        pedidosHoy.setPedidosPendientes(pedidosPorEstado.getOrDefault("Pendiente", 0L).intValue());
        pedidosHoy.setPedidosEnPreparacion(pedidosPorEstado.getOrDefault("En preparación", 0L).intValue());
        pedidosHoy.setPedidosListos(pedidosPorEstado.getOrDefault("Listo", 0L).intValue());
        pedidosHoy.setPedidosEntregados(pedidosPorEstado.getOrDefault("Entregado", 0L).intValue());
        pedidosHoy.setPedidosPagados(pedidosPorEstado.getOrDefault("Pagado", 0L).intValue());
        pedidosHoy.setPedidosCancelados(pedidosPorEstado.getOrDefault("Cancelado", 0L).intValue());

        // Generar pedidos recientes (últimos 10)
        List<DashboardResponse.PedidosHoy.PedidoInfo> pedidosRecientes = pedidosDelDia.stream()
            .sorted(Comparator.comparing(Pedido::getHoraPedido).reversed())
            .limit(10)
            .map(pedido -> {
                DashboardResponse.PedidosHoy.PedidoInfo pedidoInfo = new DashboardResponse.PedidosHoy.PedidoInfo();
                pedidoInfo.setId(pedido.getId());
                pedidoInfo.setNumeroMesa(pedido.getMesa() != null ? String.valueOf(pedido.getMesa().getNumero()) : "Para llevar");
                pedidoInfo.setEmpleado(pedido.getEmpleado().getNombre() + " " + pedido.getEmpleado().getApellido());
                pedidoInfo.setEstado(pedido.getEstadoPedido().getDescripcion());
                pedidoInfo.setHoraPedido(pedido.getHoraPedido().atDate(pedido.getFechaPedido()));
                
                // Calcular total del pedido
                BigDecimal total = pedido.getDetalles().stream()
                    .map(detalle -> BigDecimal.valueOf(detalle.getPrecioUnitario()).multiply(BigDecimal.valueOf(detalle.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                pedidoInfo.setTotal(total);
                
                return pedidoInfo;
            })
            .collect(Collectors.toList());

        pedidosHoy.setPedidosRecientes(pedidosRecientes);
        return pedidosHoy;
    }

    private DashboardResponse.TopProductos generarTopProductos(LocalDate hoy) {
        DashboardResponse.TopProductos topProductos = new DashboardResponse.TopProductos();
        
        // Obtener detalles de pedidos del día
        List<DetallePedido> detallesHoy = detallePedidoRepository.findAll().stream()
            .filter(detalle -> detalle.getPedido() != null && 
                              detalle.getPedido().getFechaPedido().equals(hoy))
            .collect(Collectors.toList());

        // Agrupar por producto
        Map<String, List<DetallePedido>> detallesPorProducto = detallesHoy.stream()
            .filter(detalle -> detalle.getProducto() != null)
            .collect(Collectors.groupingBy(detalle -> detalle.getProducto().getNombre()));

        // Generar lista de productos vendidos
        List<DashboardResponse.TopProductos.ProductoVendido> productosVendidos = detallesPorProducto.entrySet().stream()
            .map(entry -> {
                String nombreProducto = entry.getKey();
                List<DetallePedido> detallesProducto = entry.getValue();
                
                int cantidadVendida = detallesProducto.stream()
                    .mapToInt(DetallePedido::getCantidad)
                    .sum();
                
                BigDecimal totalVentas = detallesProducto.stream()
                    .map(detalle -> BigDecimal.valueOf(detalle.getPrecioUnitario()).multiply(BigDecimal.valueOf(detalle.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                String categoria = detallesProducto.get(0).getProducto().getCategoria() != null ?
                    detallesProducto.get(0).getProducto().getCategoria().getDescripcion() : "Sin categoría";
                
                DashboardResponse.TopProductos.ProductoVendido producto = new DashboardResponse.TopProductos.ProductoVendido();
                producto.setNombre(nombreProducto);
                producto.setCategoria(categoria);
                producto.setCantidadVendida(cantidadVendida);
                producto.setTotalVentas(totalVentas);
                
                return producto;
            })
            .collect(Collectors.toList());

        // Ordenar por cantidad vendida
        List<DashboardResponse.TopProductos.ProductoVendido> masVendidos = productosVendidos.stream()
            .sorted(Comparator.comparing(DashboardResponse.TopProductos.ProductoVendido::getCantidadVendida).reversed())
            .limit(5)
            .collect(Collectors.toList());

        List<DashboardResponse.TopProductos.ProductoVendido> menosVendidos = productosVendidos.stream()
            .sorted(Comparator.comparing(DashboardResponse.TopProductos.ProductoVendido::getCantidadVendida))
            .limit(5)
            .collect(Collectors.toList());

        topProductos.setMasVendidos(masVendidos);
        topProductos.setMenosVendidos(menosVendidos);

        return topProductos;
    }

    private DashboardResponse.Alertas generarAlertas(LocalDate hoy) {
        DashboardResponse.Alertas alertas = new DashboardResponse.Alertas();
        List<String> alertasCriticas = new ArrayList<>();
        List<String> alertasAdvertencia = new ArrayList<>();
        List<String> alertasInfo = new ArrayList<>();

        // Verificar pedidos pendientes por mucho tiempo
        List<Pedido> pedidosPendientes = pedidoRepository.findAll().stream()
            .filter(pedido -> pedido.getEstadoPedido().getDescripcion().equals("Pendiente") &&
                             pedido.getFechaPedido().equals(hoy))
            .collect(Collectors.toList());

        long pedidosPendientesLargos = pedidosPendientes.stream()
            .filter(pedido -> pedido.getHoraPedido().atDate(pedido.getFechaPedido()).isBefore(LocalDateTime.now().minusHours(1)))
            .count();

        if (pedidosPendientesLargos > 0) {
            alertasCriticas.add("Hay " + pedidosPendientesLargos + " pedidos pendientes por más de 1 hora");
        }

        // Verificar mesas ocupadas por mucho tiempo
        List<Mesa> mesasOcupadas = mesaRepository.findAll().stream()
            .filter(mesa -> mesa.getEstado().getDescripcion().equals("Ocupada"))
            .collect(Collectors.toList());

        if (mesasOcupadas.size() > 0) {
            alertasInfo.add("Hay " + mesasOcupadas.size() + " mesas ocupadas actualmente");
        }

        // Verificar reservas próximas
        List<Reserva> reservasHoy = reservaRepository.findAll().stream()
            .filter(reserva -> reserva.getFechaReserva().equals(hoy))
            .collect(Collectors.toList());

        if (reservasHoy.size() > 0) {
            alertasInfo.add("Hay " + reservasHoy.size() + " reservas programadas para hoy");
        }

        alertas.setAlertasCriticas(alertasCriticas);
        alertas.setAlertasAdvertencia(alertasAdvertencia);
        alertas.setAlertasInfo(alertasInfo);

        return alertas;
    }

    private List<Pago> obtenerPagosPorFecha(LocalDate fecha) {
        return pagoRepository.findAll().stream()
            .filter(pago -> pago.getFechaPago().toLocalDate().equals(fecha))
            .collect(Collectors.toList());
    }

    private List<Pago> obtenerPagosEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return pagoRepository.findAll().stream()
            .filter(pago -> {
                LocalDate fechaPago = pago.getFechaPago().toLocalDate();
                return !fechaPago.isBefore(fechaInicio) && !fechaPago.isAfter(fechaFin);
            })
            .collect(Collectors.toList());
    }

    private BigDecimal calcularTotalVentas(List<Pago> pagos) {
        return pagos.stream()
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
