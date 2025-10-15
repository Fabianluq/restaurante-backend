package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoProductoRequest {
    private String descripcion;

    public EstadoProductoRequest() {}

    public EstadoProductoRequest(String descripcion) {
        this.descripcion = descripcion;
    }
}
