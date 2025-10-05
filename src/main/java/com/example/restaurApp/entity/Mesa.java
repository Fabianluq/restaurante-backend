package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name= "mesas")
@Getter
@Setter
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numero;
    private int capacidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_estado_mesa", nullable = false)
    private EstadoMesa estado;

    public Mesa() {}
    public Mesa(int numero, int capacidad, EstadoMesa estado) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }


}
