package com.example.restaurApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaRequest {
    @NotNull
    private LocalDate fechaReserva;
    @NotNull
    private LocalTime horaReserva;
    @Min(1)
    private int cantidadPersonas;
    @Size(min = 1, max = 60)
    private String nombreCliente;
    @Size(min = 1, max = 60)
    private String apellidoCliente;
    @Email
    private String correoCliente;
    @Size(min = 6, max = 20)
    private String telefonoCliente;

    public ReservaRequest(){}

    public ReservaRequest(LocalDate fechaReserva, LocalTime horaReserva,
                          int cantidadPersonas, String nombreCliente, String apellidoCliente, String correoCliente, String telefonoCliente) {
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.cantidadPersonas = cantidadPersonas;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.correoCliente = correoCliente;
        this.telefonoCliente = telefonoCliente;

    }
}
