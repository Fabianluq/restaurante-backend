package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.EstadoMesaRequest;
import com.example.restaurApp.dto.EstadoMesaResponse;
import com.example.restaurApp.entity.EstadoMesa;

public class EstadoMesaMapper {
    public static EstadoMesaResponse toResponse(EstadoMesa estadoMesa) {
        return new EstadoMesaResponse(
                estadoMesa.getId(),
                estadoMesa.getDescripcion()
        );
    }

    public static EstadoMesa toEntity(EstadoMesaRequest Request) {
        EstadoMesa estadoMesa = new EstadoMesa();
        estadoMesa.setDescripcion(Request.getDescripcion());
        return estadoMesa;
    }
}
