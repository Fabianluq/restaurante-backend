package com.example.restaurApp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EstadoDetalleUtilTest {

    @Test
    void transicionesValidas() {
        assertTrue(EstadoDetalleUtil.esTransicionValidaDetalle("Pendiente", "En preparación"));
        assertTrue(EstadoDetalleUtil.esTransicionValidaDetalle("En preparación", "Listo"));
        assertTrue(EstadoDetalleUtil.esTransicionValidaDetalle("Pendiente", "Pendiente"));
    }

    @Test
    void transicionesInvalidas() {
        assertFalse(EstadoDetalleUtil.esTransicionValidaDetalle("Listo", "En preparación"));
        assertFalse(EstadoDetalleUtil.esTransicionValidaDetalle("Listo", "Pendiente"));
        assertFalse(EstadoDetalleUtil.esTransicionValidaDetalle("EstadoInexistente", "Pendiente"));
    }
}


