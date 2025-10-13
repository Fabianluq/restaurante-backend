package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaResponse {
    private Long id;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private int cantidadPersonas;
    private String clienteNombre;
    private String estadoReserva;

    public ReservaResponse(){}

    public ReservaResponse(Long id, LocalDate fechaReserva, LocalTime horaReserva,
                           int cantidadPersonas, String clienteNombre, String estadoReserva) {
        this.id = id;
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.cantidadPersonas = cantidadPersonas;
        this.clienteNombre = clienteNombre;
        this.estadoReserva = estadoReserva;
    }




}
