package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String contrasenia;
    private Long rolId;

    public EmpleadoRequest(){}
    public EmpleadoRequest(Long rolId,  String nombre, String apellido, String correo,
                           String telefono, String contrasenia){
        this.rolId = rolId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasenia = contrasenia;
    }
}
