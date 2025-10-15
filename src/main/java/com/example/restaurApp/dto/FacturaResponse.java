package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FacturaResponse {
    private Long pedidoId;
    private String numeroMesa; // "Para llevar" si no aplica
    private String mesero;
    private LocalDate fechaPedido;
    private LocalDateTime horaPedido;
    private List<FacturaItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal impuestos; // 0 por defecto
    private BigDecimal propina;   // 0 por defecto
    private BigDecimal total;
}


