package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaRequest {
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private int cantidadPersonas;
    private Long clienteId;
    private Long mesaId;
    private Long estadoId;

    public ReservaRequest(){}

    public ReservaRequest(LocalDate fechaReserva, LocalTime horaReserva,
                          int cantidadPersonas, Long clienteId, Long mesaId, Long estadoId) {
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.cantidadPersonas = cantidadPersonas;
        this.clienteId = clienteId;
        this.mesaId = mesaId;
        this.estadoId = estadoId;
    }
}
