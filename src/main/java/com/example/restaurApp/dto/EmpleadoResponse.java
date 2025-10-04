package com.example.restaurApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmpleadoResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String rol;

    public EmpleadoResponse() {}
}