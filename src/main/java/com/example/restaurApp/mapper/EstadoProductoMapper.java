package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.EstadoProductoResponse;
import com.example.restaurApp.dto.EstadoProductoRequest;
import com.example.restaurApp.entity.EstadoProducto;

public class EstadoProductoMapper {
    public static EstadoProductoResponse toResponse(EstadoProducto estadoProducto) {
        return new EstadoProductoResponse(
                estadoProducto.getId(),
                estadoProducto.getDescripcion()
        );
    }

    public static EstadoProducto toEntity(EstadoProductoRequest request) {
        EstadoProducto estadoProducto = new EstadoProducto();
        estadoProducto.setDescripcion(request.getDescripcion());
        return estadoProducto;
    }
}
