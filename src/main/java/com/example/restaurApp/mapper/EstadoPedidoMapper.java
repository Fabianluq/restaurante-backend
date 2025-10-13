package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.EstadoPedidoRequest;
import com.example.restaurApp.dto.EstadoPedidoResponse;
import com.example.restaurApp.entity.EstadoPedido;

public class EstadoPedidoMapper {
    public static EstadoPedidoResponse toResponse(EstadoPedido estadoPedido) {
        return new EstadoPedidoResponse(
                estadoPedido.getId(),
                estadoPedido.getDescripcion()
        );
    }

    public static EstadoPedido toEntity(EstadoPedidoRequest Request) {
        EstadoPedido estadoPedido = new EstadoPedido();
        estadoPedido.setDescripcion(Request.getDescripcion());
        return estadoPedido;
    }
}
