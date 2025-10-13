package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoPedidoRequest {
    private String descripcion;

    public EstadoPedidoRequest () {}
    public EstadoPedidoRequest(String descripcion) {
        this.descripcion = descripcion;
    }
}
