package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoProductoRequest {
    private String nombre;

    public EstadoProductoRequest() {}

    public EstadoProductoRequest(String nombre) {
        this.nombre = nombre;
    }


}
