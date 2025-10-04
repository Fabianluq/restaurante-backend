package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaRequest {
    private String nombre;
    private String descripcion;

    public CategoriaRequest(){}

    public CategoriaRequest(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

}
