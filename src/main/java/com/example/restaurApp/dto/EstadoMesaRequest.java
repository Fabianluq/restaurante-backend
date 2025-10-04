package com.example.restaurApp.dto;

import com.example.restaurApp.entity.EstadoMesa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoMesaRequest {
    private String descripcion;

    public EstadoMesaRequest () {}
    public EstadoMesaRequest(String descripcion) {
        this.descripcion = descripcion;
    }
}
