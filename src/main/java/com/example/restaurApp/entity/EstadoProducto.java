package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estado_producto")
@Getter
@Setter
public class EstadoProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;

    public EstadoProducto() {}

    public EstadoProducto(String descripcion) {
        this.descripcion = descripcion;
    }
}
