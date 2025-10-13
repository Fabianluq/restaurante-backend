package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoReservaRequest {
    private String descripcion;

    public EstadoReservaRequest () {}
    public EstadoReservaRequest(String descripcion) {
        this.descripcion = descripcion;
    }
}
