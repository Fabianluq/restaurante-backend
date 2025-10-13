package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="estado_reserva")
@Getter
@Setter
public class EstadoReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;

    public EstadoReserva() {}

    public EstadoReserva(String descripcion) {
        this.descripcion = descripcion;
    }
}

