package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.PedidoRequest;
import com.example.restaurApp.dto.PedidoResponse;
import com.example.restaurApp.dto.DetallePedidoResponse;
import com.example.restaurApp.entity.Cliente;
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.EstadoPedido;
import com.example.restaurApp.entity.Mesa;
import com.example.restaurApp.entity.Pedido;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PedidoMapper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static PedidoResponse toResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setFechaPedido(pedido.getFechaPedido() != null ? pedido.getFechaPedido().format(DATE_FMT) : null);
        response.setHoraPedido(pedido.getHoraPedido() != null ? pedido.getHoraPedido().format(TIME_FMT) : null);
        response.setEstado(pedido.getEstadoPedido().getDescripcion());
        response.setEmpleadoNombre(pedido.getEmpleado().getNombre() + " " + pedido.getEmpleado().getApellido());
        response.setClienteNombre(pedido.getCliente() != null
                ? pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido()
                : "No especificado");
        response.setMesaNumero(pedido.getMesa() != null ? pedido.getMesa().getNumero() : null);
        
        // Mapear los detalles del pedido
        if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
            List<DetallePedidoResponse> detallesResponse = pedido.getDetalles().stream()
                    .map(detalle -> new DetallePedidoResponse(
                            detalle.getId(),
                            detalle.getProducto().getNombre(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getCantidad() * detalle.getPrecioUnitario(),
                            detalle.getPedido().getId(),
                            detalle.getEstadoDetalle() != null ? detalle.getEstadoDetalle().getDescripcion() : "Pendiente"
                    ))
                    .toList();
            response.setDetalles(detallesResponse);
        }
        
        return response;
    }

    public static Pedido toEntity(PedidoRequest request, EstadoPedido estado,
                                  Empleado empleado, Mesa mesa, Cliente cliente) {

        Pedido pedido = new Pedido();
        // Las fechas y horas se generan automáticamente por el sistema
        pedido.setFechaPedido(LocalDate.now());
        pedido.setHoraPedido(LocalTime.now());
        pedido.setEstadoPedido(estado);

        if (empleado == null) {
            throw new IllegalArgumentException("El empleado que crea el pedido no puede ser nulo.");
        }
        pedido.setEmpleado(empleado);

        pedido.setMesa(mesa);
        pedido.setCliente(cliente);
        return pedido;
    }
}

