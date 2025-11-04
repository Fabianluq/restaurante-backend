package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class PedidoResponse {
    private Long id;
    private String fechaPedido;
    private String horaPedido;
    private String estado;
    private String empleadoNombre;
    private String clienteNombre;
    private Integer mesaNumero;
    private List<DetallePedidoResponse> detalles;

    public PedidoResponse() {
    }

    public PedidoResponse(Long id, String fechaPedido, String horaPedido, String estado,
                          String empleadoNombre, String clienteNombre, Integer mesaNumero) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.horaPedido = horaPedido;
        this.estado = estado;
        this.empleadoNombre = empleadoNombre;
        this.clienteNombre = clienteNombre;
        this.mesaNumero = mesaNumero;
    }
}
