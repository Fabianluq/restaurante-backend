package com.example.restaurApp.excepciones;

public class RecursoNoEncontrado extends RuntimeException {
    public RecursoNoEncontrado(String mensaje) {
        super(mensaje);
    }

}
