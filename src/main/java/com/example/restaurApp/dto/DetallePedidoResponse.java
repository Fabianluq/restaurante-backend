package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetallePedidoResponse {
    private Long id;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double totalDetalle;
    private Long pedidoId;
    private String estadoDetalle;

    public DetallePedidoResponse() {}

    public DetallePedidoResponse(Long id, String nombreProducto, int cantidad,
                                 double precioUnitario, double totalDetalle, Long pedidoId, String estadoDetalle) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.totalDetalle = totalDetalle;
        this.pedidoId = pedidoId;
        this.estadoDetalle = estadoDetalle;
    }
}
