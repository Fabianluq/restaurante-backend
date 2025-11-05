package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaResponse {
    private Long id;
    private String mensaje;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private int cantidadPersonas;
    private String clienteNombre;
    private String correoCliente;
    private String telefonoCliente;
    private String estadoReserva;
    private Integer mesaNumero;
    private Long clienteId;

    public ReservaResponse(){}

    public ReservaResponse(String mensaje, LocalDate fechaReserva, LocalTime horaReserva,
                           int cantidadPersonas, String clienteNombre, String estadoReserva) {
        this.mensaje = mensaje;
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.cantidadPersonas = cantidadPersonas;
        this.clienteNombre = clienteNombre;
        this.estadoReserva = estadoReserva;
    }
}
