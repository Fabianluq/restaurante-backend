package com.example.restaurApp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PagoRequest {
    @NotNull
    private Long pedidoId;
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal monto;
    @NotBlank
    private String metodoPago; // efectivo, tarjeta, transferencia
    private String observaciones;

    public PagoRequest() {}

    public PagoRequest(Long pedidoId, BigDecimal monto, String metodoPago, String observaciones) {
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
    }
}
