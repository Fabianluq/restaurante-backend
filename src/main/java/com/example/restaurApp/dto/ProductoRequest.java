package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {

    private String nombre;
    private String descripcion;
    private double precio;
    private Long categoriaId;
    private Long estadoId;

    ProductoRequest(){}
    ProductoRequest(String nombre, String descripcion, double precio, Long categoriaId, Long estadoId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.estadoId = estadoId;
    }

}
