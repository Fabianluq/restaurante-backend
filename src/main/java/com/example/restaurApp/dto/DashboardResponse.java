package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DashboardResponse {
    private LocalDate fechaActual;
    private LocalDateTime ultimaActualizacion;
    private MetricasGenerales metricasGenerales;
    private EstadoMesas estadoMesas;
    private PedidosHoy pedidosHoy;
    private TopProductos topProductos;
    private Alertas alertas;

    public DashboardResponse() {}

    public DashboardResponse(LocalDate fechaActual, LocalDateTime ultimaActualizacion) {
        this.fechaActual = fechaActual;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    @Getter
    @Setter
    public static class MetricasGenerales {
        private BigDecimal ventasHoy;
        private BigDecimal ventasAyer;
        private BigDecimal ventasSemana;
        private BigDecimal ventasMes;
        private int pedidosHoy;
        private int pedidosAyer;
        private int reservasHoy;
        private int clientesNuevosHoy;

        public MetricasGenerales() {}
    }

    @Getter
    @Setter
    public static class EstadoMesas {
        private int totalMesas;
        private int mesasDisponibles;
        private int mesasOcupadas;
        private int mesasReservadas;
        private int mesasMantenimiento;
        private List<MesaInfo> mesasDetalle;

        public EstadoMesas() {}

        @Getter
        @Setter
        public static class MesaInfo {
            private String numero;
            private String estado;
            private String empleadoAsignado;
            private LocalDateTime ultimaActividad;

            public MesaInfo() {}
        }
    }

    @Getter
    @Setter
    public static class PedidosHoy {
        private int totalPedidos;
        private int pedidosPendientes;
        private int pedidosEnPreparacion;
        private int pedidosListos;
        private int pedidosEntregados;
        private int pedidosPagados;
        private int pedidosCancelados;
        private List<PedidoInfo> pedidosRecientes;

        public PedidosHoy() {}

        @Getter
        @Setter
        public static class PedidoInfo {
            private Long id;
            private String numeroMesa;
            private String empleado;
            private String estado;
            private BigDecimal total;
            private LocalDateTime horaPedido;

            public PedidoInfo() {}
        }
    }

    @Getter
    @Setter
    public static class TopProductos {
        private List<ProductoVendido> masVendidos;
        private List<ProductoVendido> menosVendidos;

        public TopProductos() {}

        @Getter
        @Setter
        public static class ProductoVendido {
            private String nombre;
            private String categoria;
            private int cantidadVendida;
            private BigDecimal totalVentas;

            public ProductoVendido() {}
        }
    }

    @Getter
    @Setter
    public static class Alertas {
        private List<String> alertasCriticas;
        private List<String> alertasAdvertencia;
        private List<String> alertasInfo;

        public Alertas() {}
    }
}
