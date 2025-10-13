package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name= "reservas")
@Getter
@Setter
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    @Column(name = "hora_reserva", nullable = false)
    private LocalTime horaReserva;

    @Column(name = "cantidad_personas", nullable = false)
    private int cantidadPersonas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_mesa", nullable = false)
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_estado_reserva", nullable = false)
    private EstadoReserva estadoReserva;

    public Reserva() {
    }

    public Reserva(LocalDate fechaReserva, LocalTime horaReserva, int cantidadPersonas, Cliente cliente,
                   Mesa mesa, EstadoReserva estado) {
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.cantidadPersonas = cantidadPersonas;
        this.cliente = cliente;
        this.mesa = mesa;
        this.estadoReserva = estado;
    }
}


