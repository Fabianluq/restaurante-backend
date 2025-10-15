package com.example.restaurApp.excepciones;

public class Validacion extends RuntimeException {
    public Validacion(String message) {
        super(message);
    }
}
