package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PedidoResponse {
    private Long id;
    private LocalDate fechaPedido;
    private LocalTime horaPedido;
    private String estado;
    private String empleadoNombre;
    private String clienteNombre;
    private Integer mesaCapacidad;

    public PedidoResponse() {
    }

    public PedidoResponse(Long id, LocalDate fechaPedido, LocalTime horaPedido, String estado,
                          String empleadoNombre, String clienteNombre, Integer mesaCapacidad) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.horaPedido = horaPedido;
        this.estado = estado;
        this.empleadoNombre = empleadoNombre;
        this.clienteNombre = clienteNombre;
        this.mesaCapacidad = mesaCapacidad;
    }
}
