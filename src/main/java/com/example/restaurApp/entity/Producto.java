package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_estado_producto", nullable = false)
    private EstadoProducto estadoProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_categoria", nullable = false)
    private Categoria categoria;

    public Producto() {}
    public Producto(String nombre, String descripcion, double precio,  EstadoProducto estadoProducto,
                    Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estadoProducto = estadoProducto;
        this.categoria = categoria;
    }
}
