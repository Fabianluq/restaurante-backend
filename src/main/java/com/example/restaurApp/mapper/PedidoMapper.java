package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.PedidoRequest;
import com.example.restaurApp.dto.PedidoResponse;
import com.example.restaurApp.entity.*;

public class PedidoMapper {

    public static PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getFechaPedido(),
                pedido.getHoraPedido(),
                pedido.getEstadoPedido().getDescripcion(),
                pedido.getEmpleado().getNombre(),
                pedido.getCliente().getNombre(),
                pedido.getMesa().getCapacidad()
        );
    }

    public static Pedido toEntity(PedidoRequest request, EstadoPedido estado,
                                  Empleado empleado, Mesa mesa, Cliente cliente) {
        Pedido pedido = new Pedido();
        pedido.setFechaPedido(request.getFechaPedido());
        pedido.setHoraPedido(request.getHoraPedido());
        pedido.setEstadoPedido(estado);
        pedido.setEmpleado(empleado);
        pedido.setMesa(mesa);
        pedido.setCliente(cliente);
        return pedido;
    }
}
