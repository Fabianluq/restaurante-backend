package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReporteVentasResponse {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipoReporte;
    private BigDecimal totalVentas;
    private int totalPedidos;
    private int totalPagos;
    private BigDecimal promedioPorPedido;
    private List<VentaPorDia> ventasPorDia;
    private List<VentaPorCategoria> ventasPorCategoria;
    private List<VentaPorEmpleado> ventasPorEmpleado;

    public ReporteVentasResponse() {}

    public ReporteVentasResponse(LocalDate fechaInicio, LocalDate fechaFin, String tipoReporte,
                               BigDecimal totalVentas, int totalPedidos, int totalPagos,
                               BigDecimal promedioPorPedido) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipoReporte = tipoReporte;
        this.totalVentas = totalVentas;
        this.totalPedidos = totalPedidos;
        this.totalPagos = totalPagos;
        this.promedioPorPedido = promedioPorPedido;
    }

    @Getter
    @Setter
    public static class VentaPorDia {
        private LocalDate fecha;
        private BigDecimal totalVentas;
        private int totalPedidos;

        public VentaPorDia() {}

        public VentaPorDia(LocalDate fecha, BigDecimal totalVentas, int totalPedidos) {
            this.fecha = fecha;
            this.totalVentas = totalVentas;
            this.totalPedidos = totalPedidos;
        }
    }

    @Getter
    @Setter
    public static class VentaPorCategoria {
        private String categoria;
        private BigDecimal totalVentas;
        private int totalProductos;
        private int totalCantidad;

        public VentaPorCategoria() {}

        public VentaPorCategoria(String categoria, BigDecimal totalVentas, int totalProductos, int totalCantidad) {
            this.categoria = categoria;
            this.totalVentas = totalVentas;
            this.totalProductos = totalProductos;
            this.totalCantidad = totalCantidad;
        }
    }

    @Getter
    @Setter
    public static class VentaPorEmpleado {
        private String nombreEmpleado;
        private String rol;
        private BigDecimal totalVentas;
        private int totalPedidos;

        public VentaPorEmpleado() {}

        public VentaPorEmpleado(String nombreEmpleado, String rol, BigDecimal totalVentas, int totalPedidos) {
            this.nombreEmpleado = nombreEmpleado;
            this.rol = rol;
            this.totalVentas = totalVentas;
            this.totalPedidos = totalPedidos;
        }
    }
}
