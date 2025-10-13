package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.EstadoReservaRequest;
import com.example.restaurApp.dto.EstadoReservaResponse;
import com.example.restaurApp.entity.EstadoReserva;

public class EstadoReservaMapper {
    public static EstadoReservaResponse toResponse(EstadoReserva estadoReserva) {
        return new EstadoReservaResponse(
                estadoReserva.getId(),
                estadoReserva.getDescripcion()
        );
    }

    public static EstadoReserva toEntity(EstadoReservaRequest Request) {
        EstadoReserva estadoReserva = new EstadoReserva();
        estadoReserva.setDescripcion(Request.getDescripcion());
        return estadoReserva;
    }
}
