package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_pedidos")
@Getter
@Setter
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_pedido", nullable = false)
    private Pedido pedido;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_pedido", nullable = false)
    private EstadoPedido estadoPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_detalle", nullable = false)
    private EstadoDetalle estadoDetalle;

    private int cantidad;
    private double precioUnitario;


    public DetallePedido() {
    }

    public DetallePedido(Long id, Pedido pedido, Producto producto, EstadoPedido estadoPedido,
                         EstadoDetalle estadoDetalle, int cantidad, double precioUnitario) {
        this.id = id;
        this.pedido = pedido;
        this.producto = producto;
        this.estadoPedido = estadoPedido;
        this.estadoDetalle = estadoDetalle;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
}
