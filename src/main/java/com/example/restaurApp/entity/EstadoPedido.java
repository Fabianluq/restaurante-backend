package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="estado_pedido")
@Getter
@Setter
public class EstadoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;

    public EstadoPedido() {}

    public EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }
}

