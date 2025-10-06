package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoMesaRequest {
    private String descripcion;

    public EstadoMesaRequest() {
    }

    public EstadoMesaRequest(String descripcion) {
        this.descripcion = descripcion;
    }
}
