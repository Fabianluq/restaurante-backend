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
        detalle.setPrecioUnitario(producto.getPrecio());
        return detalle;
    }

    public static DetallePedidoResponse toResponse(DetallePedido detalle) {
        double total = detalle.getCantidad() * detalle.getPrecioUnitario();

        return new DetallePedidoResponse(
                detalle.getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                total,
                detalle.getPedido().getId(),
                detalle.getEstadoDetalle() != null ? detalle.getEstadoDetalle().getDescripcion() : "Pendiente"
        );
    }
}
