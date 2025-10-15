package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoProductoResponse {
    private Long id;
    private String descripcion;

    public EstadoProductoResponse() {}
    public EstadoProductoResponse(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
}
