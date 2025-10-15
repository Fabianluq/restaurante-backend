package com.example.restaurApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoRequest {
    // fechaPedido y horaPedido se generan automáticamente - no se reciben en el request
    @NotNull
    private Long estadoId;
    private Long mesaId;
    private boolean paraLlevar;
    
    // Datos del cliente - SOLO necesarios para pedidos para llevar
    @Size(min = 1, max = 60)
    private String nombreCliente;
    @Size(min = 1, max = 60)
    private String apellidoCliente;
    @Email
    private String correoCliente;
    @Size(min = 6, max = 20)
    private String telefonoCliente;

    public PedidoRequest() {}

    public PedidoRequest(Long estadoId, Long mesaId, boolean paraLlevar) {
        this.estadoId = estadoId;
        this.mesaId = mesaId;
        this.paraLlevar = paraLlevar;
    }
    
    // Constructor para pedidos para llevar con datos del cliente
    public PedidoRequest(Long estadoId, boolean paraLlevar, 
                        String nombreCliente, String apellidoCliente, 
                        String correoCliente, String telefonoCliente) {
        this.estadoId = estadoId;
        this.mesaId = null; // Para llevar no tiene mesa
        this.paraLlevar = paraLlevar;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.correoCliente = correoCliente;
        this.telefonoCliente = telefonoCliente;
    }
}
