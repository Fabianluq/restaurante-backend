package com.example.restaurApp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetallePedidoRequest {
    @NotNull
    private Long productoId;
    @Min(1)
    private int cantidad;

    public DetallePedidoRequest() {}

    public DetallePedidoRequest(Long productoId, int cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }
}
