package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetallePedidoResponse {
    private Long id;
    private int cantidad;
    private double precioUnitario;
    private String nombreProducto;
    private Long pedidoId;

    public DetallePedidoResponse() {
    }

    public DetallePedidoResponse(Long id, int cantidad, double precioUnitario, String nombreProducto, Long pedidoId) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.nombreProducto = nombreProducto;
        this.pedidoId = pedidoId;
    }
}
