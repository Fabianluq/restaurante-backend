package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoPedidoResponse {
    private Long id;
    private String descripcion;

    public EstadoPedidoResponse() {}

    public EstadoPedidoResponse(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
}
