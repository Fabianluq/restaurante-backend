package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;


    public ClienteRequest(){}

    public ClienteRequest(String nombre, String apellido, String correo, String telefono){

        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;

    }
}
