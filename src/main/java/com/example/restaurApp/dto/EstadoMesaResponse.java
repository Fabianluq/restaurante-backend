package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoMesaResponse {
    private Long id;
    private String descripcion;

    public EstadoMesaResponse() {}

    public EstadoMesaResponse(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
}
