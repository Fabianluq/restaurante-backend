package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioContrasenaRequest {
    private String contrasenaActual;
    private String nuevaContrasena;
    private String confirmarContrasena;

    public CambioContrasenaRequest() {}

    public CambioContrasenaRequest(String contrasenaActual, String nuevaContrasena, String confirmarContrasena) {
        this.contrasenaActual = contrasenaActual;
        this.nuevaContrasena = nuevaContrasena;
        this.confirmarContrasena = confirmarContrasena;
    }
}
