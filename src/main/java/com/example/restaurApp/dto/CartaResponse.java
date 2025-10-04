package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartaResponse {
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;

    public CartaResponse() {
    }

    public CartaResponse(String nombre, String descripcion, double precio, String categoria ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;



    }
}
