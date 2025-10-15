package com.example.restaurApp.util;

import com.example.restaurApp.entity.EstadoPedido;

import java.util.Arrays;
import java.util.List;

public class EstadoPedidoUtil {
    
    // Definir el flujo válido de estados
    private static final List<String> FLUJO_ESTADOS = Arrays.asList(
        "Pendiente",
        "En preparación", 
        "Listo",
        "Entregado",
        "Pagado"
    );
    
    // Estados que permiten cancelación
    private static final List<String> ESTADOS_CANCELABLES = Arrays.asList(
        "Pendiente",
        "En preparación"
    );
    
    // Estados finales (no se puede cambiar)
    private static final List<String> ESTADOS_FINALES = Arrays.asList(
        "Cancelado",
        "Pagado"
    );

    /**
     * Valida si es posible cambiar de un estado a otro
     */
    public static boolean esTransicionValida(String estadoActual, String estadoDestino) {
        // No se puede cambiar estados finales
        if (ESTADOS_FINALES.contains(estadoActual)) {
            return false;
        }
        
        // No se puede cambiar a estados finales desde cualquier estado
        if (ESTADOS_FINALES.contains(estadoDestino)) {
            return true; // Los estados finales se pueden aplicar desde cualquier estado válido
        }
        
        // Verificar flujo secuencial
        int indiceActual = FLUJO_ESTADOS.indexOf(estadoActual);
        int indiceDestino = FLUJO_ESTADOS.indexOf(estadoDestino);
        
        if (indiceActual == -1 || indiceDestino == -1) {
            return false; // Estados no válidos
        }
        
        // Solo se puede avanzar en el flujo o mantenerse en el mismo estado
        return indiceDestino >= indiceActual;
    }

    /**
     * Valida si un estado permite cancelación
     */
    public static boolean esCancelable(String estado) {
        return ESTADOS_CANCELABLES.contains(estado);
    }

    /**
     * Valida si un estado es final
     */
    public static boolean esEstadoFinal(String estado) {
        return ESTADOS_FINALES.contains(estado);
    }

    /**
     * Obtiene el siguiente estado válido en el flujo
     */
    public static String getSiguienteEstado(String estadoActual) {
        int indice = FLUJO_ESTADOS.indexOf(estadoActual);
        if (indice == -1 || indice >= FLUJO_ESTADOS.size() - 1) {
            return null; // No hay siguiente estado
        }
        return FLUJO_ESTADOS.get(indice + 1);
    }

    /**
     * Obtiene el estado anterior en el flujo
     */
    public static String getEstadoAnterior(String estadoActual) {
        int indice = FLUJO_ESTADOS.indexOf(estadoActual);
        if (indice <= 0) {
            return null; // No hay estado anterior
        }
        return FLUJO_ESTADOS.get(indice - 1);
    }

    /**
     * Valida si un rol puede cambiar a un estado específico
     */
    public static boolean rolPuedeCambiarEstado(String rol, String estadoDestino) {
        switch (rol.toLowerCase()) {
            case "mesero":
                return Arrays.asList("Entregado", "Cancelado").contains(estadoDestino);
            case "cocinero":
                return Arrays.asList("En preparación", "Listo").contains(estadoDestino);
            case "administrador":
                return true; // El administrador puede cambiar a cualquier estado
            default:
                return false;
        }
    }

    /**
     * Obtiene los estados disponibles para un rol específico
     */
    public static List<String> getEstadosDisponiblesParaRol(String rol) {
        switch (rol.toLowerCase()) {
            case "mesero":
                return Arrays.asList("Entregado", "Cancelado");
            case "cocinero":
                return Arrays.asList("En preparación", "Listo");
            case "administrador":
                return FLUJO_ESTADOS;
            default:
                return Arrays.asList();
        }
    }

    /**
     * Valida el tiempo máximo permitido en cada estado (en minutos)
     */
    public static int getTiempoMaximoEstado(String estado) {
        switch (estado.toLowerCase()) {
            case "pendiente":
                return 30; // 30 minutos máximo en pendiente
            case "en preparación":
                return 60; // 1 hora máximo en preparación
            case "listo":
                return 15; // 15 minutos máximo listo para entregar
            case "entregado":
                return 10; // 10 minutos máximo entregado antes de pagar
            default:
                return 0;
        }
    }

    /**
     * Obtiene un mensaje descriptivo del flujo de estados
     */
    public static String getMensajeFlujoEstados() {
        return "Flujo de estados: " + String.join(" → ", FLUJO_ESTADOS) + 
               " | Estados cancelables: " + String.join(", ", ESTADOS_CANCELABLES);
    }
}
