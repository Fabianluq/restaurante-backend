
package com.example.restaurApp.excepciones;

public class EmpleadoInactivoException extends RuntimeException {
    public EmpleadoInactivoException(String message) {
        super(message);
    }
}
