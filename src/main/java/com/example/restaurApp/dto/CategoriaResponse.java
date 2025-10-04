package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaResponse {
    private Long id;
    private String nombre;
    private String descripcion;

    public CategoriaResponse() {}

    public CategoriaResponse(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

}
