package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PedidoRequest {
    private LocalDate fechaPedido;
    private LocalTime horaPedido;
    private Long estadoId;
    private Long empleadoId;
    private Long mesaId;
    private Long clienteId;

    public PedidoRequest() {}

    public PedidoRequest(LocalDate fechaPedido, LocalTime horaPedido,
                         Long estadoId, Long empleadoId, Long mesaId, Long clienteId) {
        this.fechaPedido = fechaPedido;
        this.horaPedido = horaPedido;
        this.estadoId = estadoId;
        this.empleadoId = empleadoId;
        this.mesaId = mesaId;
        this.clienteId = clienteId;
    }
}
