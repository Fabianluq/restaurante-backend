package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 50)
    private String metodoPago; // efectivo, tarjeta, transferencia

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    @Column(length = 500)
    private String observaciones;

    public Pago() {}

    public Pago(Pedido pedido, BigDecimal monto, String metodoPago, String observaciones) {
        this.pedido = pedido;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = LocalDateTime.now();
        this.observaciones = observaciones;
    }
}
