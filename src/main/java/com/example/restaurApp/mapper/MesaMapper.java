package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.MesaRequest;
import com.example.restaurApp.dto.MesaResponse;
import com.example.restaurApp.entity.EstadoMesa;
import com.example.restaurApp.entity.Mesa;

public class MesaMapper {
    public static MesaResponse toResponse(Mesa mesa) {
       return new MesaResponse(
               mesa.getId(),
               mesa.getCapacidad(),
               mesa.getEstado().getDescripcion()
       );
    }

    public static Mesa toEntity(MesaRequest request, EstadoMesa estado) {
                Mesa mesa = new Mesa ();
                mesa.setNumero(request.getNumero());
                mesa.setCapacidad(request.getCapacidad());
                mesa.setEstado(estado);
                return mesa;
    }


}
