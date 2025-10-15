package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.PagoRequest;
import com.example.restaurApp.dto.PagoResponse;
import com.example.restaurApp.entity.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public PagoResponse toResponse(Pago pago) {
        if (pago == null) {
            return null;
        }

        String numeroMesa = null;
        String nombreCliente = null;

        if (pago.getPedido() != null) {
            if (pago.getPedido().getMesa() != null) {
                numeroMesa = String.valueOf(pago.getPedido().getMesa().getNumero());
            }
            if (pago.getPedido().getCliente() != null) {
                nombreCliente = pago.getPedido().getCliente().getNombre() + " " + 
                              pago.getPedido().getCliente().getApellido();
            }
        }

        return new PagoResponse(
            pago.getId(),
            pago.getPedido() != null ? pago.getPedido().getId() : null,
            pago.getMonto(),
            pago.getMetodoPago(),
            pago.getFechaPago(),
            pago.getObservaciones(),
            numeroMesa,
            nombreCliente
        );
    }
}
