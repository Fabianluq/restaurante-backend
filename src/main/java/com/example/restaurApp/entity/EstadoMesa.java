package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name ="estado_mesa")
@Getter
@Setter
public class EstadoMesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;

    public EstadoMesa() {}

    public EstadoMesa(String descripcion) {
        this.descripcion = descripcion;
    }
}
