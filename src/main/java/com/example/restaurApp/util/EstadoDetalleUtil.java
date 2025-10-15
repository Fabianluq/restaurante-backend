package com.example.restaurApp.util;

import java.util.Arrays;
import java.util.List;

public class EstadoDetalleUtil {
    
    // Definir el flujo válido de estados para detalles
    private static final List<String> FLUJO_ESTADOS_DETALLE = Arrays.asList(
        "Pendiente",
        "En preparación", 
        "Listo"
    );
    
    // Estados que permiten retroceso
    private static final List<String> ESTADOS_RETROCESO = Arrays.asList(
        "En preparación"  // Se puede volver a Pendiente por error o falta de ingredientes
    );

    /**
     * Valida si es posible cambiar de un estado a otro en un detalle
     */
    public static boolean esTransicionValidaDetalle(String estadoActual, String estadoDestino) {
        // Verificar flujo secuencial
        int indiceActual = FLUJO_ESTADOS_DETALLE.indexOf(estadoActual);
        int indiceDestino = FLUJO_ESTADOS_DETALLE.indexOf(estadoDestino);
        
        if (indiceActual == -1 || indiceDestino == -1) {
            return false; // Estados no válidos
        }
        
        // Se puede avanzar en el flujo
        if (indiceDestino > indiceActual) {
            return true;
        }
        
        // Se puede retroceder solo desde "En preparación" a "Pendiente"
        if (estadoActual.equals("En preparación") && estadoDestino.equals("Pendiente")) {
            return true;
        }
        
        // Se puede mantener en el mismo estado
        return indiceDestino == indiceActual;
    }

    /**
     * Obtiene el siguiente estado válido en el flujo
     */
    public static String getSiguienteEstadoDetalle(String estadoActual) {
        int indice = FLUJO_ESTADOS_DETALLE.indexOf(estadoActual);
        if (indice == -1 || indice >= FLUJO_ESTADOS_DETALLE.size() - 1) {
            return null; // No hay siguiente estado
        }
        return FLUJO_ESTADOS_DETALLE.get(indice + 1);
    }

    /**
     * Obtiene el estado anterior en el flujo (solo si es válido retroceder)
     */
    public static String getEstadoAnteriorDetalle(String estadoActual) {
        int indice = FLUJO_ESTADOS_DETALLE.indexOf(estadoActual);
        if (indice <= 0) {
            return null; // No hay estado anterior
        }
        
        // Solo se puede retroceder desde "En preparación" a "Pendiente"
        if (estadoActual.equals("En preparación")) {
            return "Pendiente";
        }
        
        return null;
    }

    /**
     * Obtiene un mensaje descriptivo del flujo de estados de detalles
     */
    public static String getMensajeFlujoEstadosDetalle() {
        return "Flujo de estados de detalles: " + String.join(" → ", FLUJO_ESTADOS_DETALLE) + 
               " | Retroceso permitido: En preparación → Pendiente";
    }
}
