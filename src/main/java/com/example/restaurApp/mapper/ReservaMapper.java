package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.ReservaRequest;
import com.example.restaurApp.dto.ReservaResponse;
import com.example.restaurApp.entity.Cliente;
import com.example.restaurApp.entity.EstadoReserva;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.entity.Reserva;

public class ReservaMapper {
    public static ReservaResponse toResponse(Reserva reserva){
        return new ReservaResponse(
                reserva.getId(),
                reserva.getFechaReserva(),
                reserva.getHoraReserva(),
                reserva.getCantidadPersonas(),
                reserva.getCliente().getNombre(),
                reserva.getEstadoReserva().getDescripcion()
        );
    }

    public static Reserva toEntity(ReservaRequest request, Cliente cliente, Mesa mesa, EstadoReserva estadoReserva) {
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(request.getFechaReserva());
        reserva.setHoraReserva(request.getHoraReserva());
        reserva.setCantidadPersonas(request.getCantidadPersonas());
        reserva.setCliente(cliente);
        reserva.setMesa(mesa);
        reserva.setEstadoReserva(estadoReserva);
        return reserva;
    }
}
