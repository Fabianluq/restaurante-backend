package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FacturaItemResponse {
    private Long detalleId;
    private String producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
}


