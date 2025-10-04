package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoProductoResponse {
    private Long id;
    private String nombre;

    public EstadoProductoResponse() {}
    public EstadoProductoResponse(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
