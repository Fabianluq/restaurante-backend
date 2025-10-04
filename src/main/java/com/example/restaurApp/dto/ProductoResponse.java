package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoriaId;
    private String estadoId;

    public ProductoResponse () {}

    public ProductoResponse(Long id, String nombre, String descripcion, double precio, String categoriaId, String estadoId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.estadoId = estadoId;
    }
}
