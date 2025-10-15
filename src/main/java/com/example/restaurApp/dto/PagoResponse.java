package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PagoResponse {
    private Long id;
    private Long pedidoId;
    private BigDecimal monto;
    private String metodoPago;
    private LocalDateTime fechaPago;
    private String observaciones;
    private String numeroMesa;
    private String nombreCliente;

    public PagoResponse() {}

    public PagoResponse(Long id, Long pedidoId, BigDecimal monto, String metodoPago, 
                       LocalDateTime fechaPago, String observaciones, String numeroMesa, String nombreCliente) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.observaciones = observaciones;
        this.numeroMesa = numeroMesa;
        this.nombreCliente = nombreCliente;
    }
}
