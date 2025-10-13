package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetallePedidoRequest {
    private Long pedidoId;
    private Long productoId;
    private int cantidad;
    private double precioUnitario;

    public DetallePedidoRequest() {
    }

    public DetallePedidoRequest(Long pedidoId, Long productoId, int cantidad, double precioUnitario) {
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
}
