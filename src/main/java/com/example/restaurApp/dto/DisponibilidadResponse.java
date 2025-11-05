package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class DisponibilidadResponse {
    private boolean disponible;
    private String mensaje;
    private Integer mesaNumero;
    private Integer capacidadMesa;

    public DisponibilidadResponse() {}

    public DisponibilidadResponse(boolean disponible, String mensaje) {
        this.disponible = disponible;
        this.mensaje = mensaje;
    }

    public DisponibilidadResponse(boolean disponible, String mensaje, Integer mesaNumero, Integer capacidadMesa) {
        this.disponible = disponible;
        this.mensaje = mensaje;
        this.mesaNumero = mesaNumero;
        this.capacidadMesa = capacidadMesa;
    }
}

