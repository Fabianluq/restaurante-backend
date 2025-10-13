package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.DetallePedidoRequest;
import com.example.restaurApp.dto.DetallePedidoResponse;
import com.example.restaurApp.entity.DetallePedido;
import com.example.restaurApp.entity.Pedido;
import com.example.restaurApp.entity.Producto;

public class DetallePedidoMapper {

    public static DetallePedido toEntity(DetallePedidoRequest request, Pedido pedido, Producto producto) {
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(request.getPrecioUnitario());
        return detalle;
    }

    public static DetallePedidoResponse toResponse(DetallePedido detalle) {
        return new DetallePedidoResponse(
                detalle.getId(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getProducto().getNombre(),
                detalle.getPedido().getId()
        );
    }
}
