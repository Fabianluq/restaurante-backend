package com.example.restaurApp.excepciones;

public class ReservaNoDisponible extends RuntimeException {
    public ReservaNoDisponible(String mensaje) {
        super(mensaje);
    }
}
