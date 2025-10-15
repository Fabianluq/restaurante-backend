package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    @Column(name = "hora_pedido", nullable = false)
    private LocalTime horaPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_estadoPedido", nullable = false)
    private EstadoPedido estadoPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_empleado", nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_mesa", nullable = false)
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "id_cliente", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {}

    public Pedido(Long id, LocalDate fechaPedido, LocalTime horaPedido, EstadoPedido estadoPedido,
                  Empleado empleado, Mesa mesa, Cliente cliente) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.horaPedido = horaPedido;
        this.estadoPedido = estadoPedido;
        this.empleado = empleado;
        this.mesa = mesa;
        this.cliente = cliente;
    }
}
