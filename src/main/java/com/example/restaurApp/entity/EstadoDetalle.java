package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estado_detalle")
@Getter
@Setter
public class EstadoDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;

    public EstadoDetalle() {}

    public EstadoDetalle(String descripcion) {
        this.descripcion = descripcion;
    }
}
